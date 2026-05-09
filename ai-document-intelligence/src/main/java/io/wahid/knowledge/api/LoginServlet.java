package io.wahid.knowledge.api;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.wahid.knowledge.security.JwtFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        JwtFilter.sendCorsHeaders(req, resp);
        LOGGER.info("LoginServlet doOptions");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("LoginServlet doPost--------------> setting cors");
        JwtFilter.sendCorsHeaders(req, resp);
        resp.setContentType("application/json");

        // Read JSON body
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        LOGGER.log(Level.INFO, "request-> {0}", sb);
        JSONObject json = new JSONObject(sb.toString());
        String idToken = json.optString("idToken", null);

        LOGGER.info("LoginServlet doPost--------------> checking idtoken");
        try {
            if (idToken == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing idToken\"}");
                return;
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        try {
            // Verify Firebase ID token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            // Return success
            resp.setStatus(HttpServletResponse.SC_OK);
            JSONObject result = new JSONObject();
            result.put("message", "Login successful");
            result.put("email", decodedToken.getEmail());
            result.put("access_token", decodedToken);
            resp.getWriter().write(result.toString());

        } catch (IOException | FirebaseAuthException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
