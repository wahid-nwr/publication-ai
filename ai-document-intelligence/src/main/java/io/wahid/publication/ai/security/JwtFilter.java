package io.wahid.publication.ai.security;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.*;
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

public class JwtFilter implements Filter {

    private final JwtConfig cfg;
    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "http://localhost:8080", "http://localhost:8080/",
            "http://127.0.0.1:8080", "http://127.0.0.1:8080/",
            "http://34.59.213.229:8080", "http://34.59.213.229:8080/"
    );

    public JwtFilter(JwtConfig cfg, JWKSource<SecurityContext> jwkSource) {
        this.cfg = cfg;

        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(cfg.getJwsAlgorithm(), jwkSource);

        jwtProcessor.setJWSKeySelector(keySelector);

        // We will validate claims manually
        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> { });
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        sendCorsHeaders(request, response);
        if (request.getMethod().equals("OPTIONS")) {
            System.out.println("got options request, sending cors headers");
            res.getWriter().write("HTTP/1.1 200 OK\r\n\r\n");
            return;
        }
        // public routes (login page, static assets)
        if (isPublicRoute(request)) {
            chain.doFilter(req, res);
            return;
        }

        // protected routes (all other routes)
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            unauthorized(response, "Missing Authorization header");
            return;
        }

        if (TokenVerifier.verify(authHeader) == null) {
            unauthorized(response, "Invalid token");
            return;
        }
        chain.doFilter(req, res);
    }

    public static void sendCorsHeaders(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");
        System.out.println("request origin-> " + origin);
        if (origin != null) {
            System.out.println("matched->>" + ALLOWED_ORIGINS.contains(origin));
        }
        // Allow only trusted origins
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            System.out.println("setting allow origin true!!!!!");
            resp.setHeader("Access-Control-Allow-Origin", origin);
        }

        resp.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Vary", "Origin"); // avoid caching incorrect CORS headers
    }

    private boolean isPublicRoute(HttpServletRequest req) {
        String path = req.getRequestURI();
        return path.startsWith("/public")
                || path.equals("/auth/login")
                || path.endsWith(".html")
                || path.endsWith(".js")
                || path.endsWith(".css");
    }

    private void unauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(401);
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
