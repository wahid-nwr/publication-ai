package io.wahid.publication.ai.security;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
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
            "http://34.59.213.229:8081",
            "https://publication-ai-652346505611.us-central1.run.app"
    );

    public JwtFilter(JwtConfig cfg, JWKSource<SecurityContext> jwkSource) {

        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(cfg.getJwsAlgorithm(), jwkSource);

        jwtProcessor.setJWSKeySelector(keySelector);

        // We will validate claims manually
        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
        });
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

        if (TokenVerifier.verify(authHeader) == null) {
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
