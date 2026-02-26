package io.wahid.publication.ai.security;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JwtFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(JwtFilter.class.getName());
    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "http://localhost:8081",
            "http://127.0.0.1:8081",
            "https://ragops.online",
            "https://publication-ai-652346505611.us-central1.run.app"
    );
    private final JwtConfig cfg;
    private final DefaultJWTProcessor<SecurityContext> jwtProcessor;
    /*public JwtFilter(JwtConfig cfg, JWKSource<SecurityContext> jwkSource) {

        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(cfg.getJwsAlgorithm(), jwkSource);

        jwtProcessor.setJWSKeySelector(keySelector);

        // We will validate claims manually
        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
        });
    }*/
    public JwtFilter(JwtConfig cfg) throws Exception {
        this.cfg = cfg;

        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();

        // 1️⃣ Load Keycloak JWKS endpoint
        JWKSource<SecurityContext> jwkSource =
                JWKSourceBuilder.create(URI.create(cfg.getJwksUri()).toURL())
                        .cache(
                                5 * 60_000L,    // TTL: 5 minutes
                                30_000L         // Refresh ahead: 30 seconds
                        )
                        .build();

        // 2️⃣ Configure signature verification (RS256)
        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(
                        cfg.getJwsAlgorithm(),
                        jwkSource
                );

        processor.setJWSKeySelector(keySelector);

        // 3️⃣ Claims validation
        processor.setJWTClaimsSetVerifier((claims, context) -> {

            // Validate issuer
            if (!cfg.getIssuer().equals(claims.getIssuer())) {
                throw new BadJWTException("Invalid issuer");
            }

            // Validate expiration
            Date exp = claims.getExpirationTime();
            if (exp == null || new Date().after(exp)) {
                throw new BadJWTException("Token expired");
            }

            // Validate audience
            if (cfg.getAudience() != null &&
                    !claims.getAudience().contains(cfg.getAudience())) {
                throw new BadJWTException("Invalid audience");
            }
        });

        this.jwtProcessor = processor;
    }

    public JWTClaimsSet validate(String token) throws Exception {
        LOGGER.log(Level.INFO, "token-> {0}", token);
        LOGGER.log(Level.INFO, "config-> uri={0}, issuer={1}, audience={2}",
                new Object[]{this.cfg.getJwksUri(), this.cfg.getIssuer(), this.cfg.getAudience()});
        return jwtProcessor.process(token, null);
    }

    public static void sendCorsHeaders(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");
        if (origin != null) {
            origin = origin.replaceAll("/$", "");
        }

        LOGGER.log(Level.INFO, "request origin-> {0}", origin);
        if (origin != null) {
            LOGGER.log(Level.INFO, "matched->> {0}", ALLOWED_ORIGINS.contains(origin));
        }
        // Allow only trusted origins
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            LOGGER.info("setting allow origin true!");
            resp.setHeader("Access-Control-Allow-Origin", origin);
        }

        resp.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Vary", "Origin"); // avoid caching incorrect CORS headers
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        sendCorsHeaders(request, response);
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            LOGGER.info("got options request, sending cors headers");
            response.setStatus(HttpServletResponse.SC_OK);
            return; // stop filter chain
        }
        // public routes (login page, static assets)
        if (isPublicRoute(request)) {
            chain.doFilter(req, res);
            return;
        }

        // protected routes (all other routes)
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            unauthorized(request, response, "Missing Authorization header");
            return;
        }

        /*if (TokenVerifier.verify(authHeader) == null) {
            unauthorized(request, response, "Invalid token");
            return;
        }*/
        try {
            String token = authHeader.substring(7); // remove "Bearer "
            JWTClaimsSet claims = validate(token);

            // optionally store claims for controllers
            request.setAttribute("jwtClaims", claims);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "JWT validation failed", e);
            unauthorized(request, response, "Invalid token");
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isPublicRoute(HttpServletRequest req) {
        String path = req.getRequestURI();
        return path.startsWith("/public")
                || path.equals("/auth/login")
                || path.endsWith(".html")
                || path.endsWith(".js")
                || path.endsWith(".css");
    }

    private void unauthorized(HttpServletRequest request, HttpServletResponse response, String msg) throws IOException {
        sendCorsHeaders(request, response);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + msg + "\"}");
    }

    private String extractTenant(JWTClaimsSet claims, Map<String, Object> firebase) throws ParseException {
        // Custom claim first
        Object t = claims.getClaim("tenant");
        if (t != null) return t.toString();

        // Optional: custom added inside firebase
        if (firebase != null) {
            Object t2 = firebase.get("tenant");
            if (t2 != null) return t2.toString();
        }

        // Fallback: email-domain mapping
        String email = claims.getStringClaim("email");
        if (email != null && email.contains("@")) {
            String domain = email.substring(email.indexOf('@') + 1);
            return domain.replace(".", "-");  // e.g. tenant from domain
        }

        return "default";
    }

    private List<String> extractRoles(JWTClaimsSet claims) {
        Object rolesObj = claims.getClaim("roles");

        if (rolesObj instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }

        return List.of("USER");
    }
}
