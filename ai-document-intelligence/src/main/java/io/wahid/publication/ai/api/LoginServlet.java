package io.wahid.publication.ai.api;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import io.wahid.publication.ai.security.JwtFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/auth/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        JwtFilter.sendCorsHeaders(req, resp);
        System.out.println("LoginServlet doOptions");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("LoginServlet doPost--------------> setting cors");
        JwtFilter.sendCorsHeaders(req, resp);
        resp.setContentType("application/json");

        // Read JSON body
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }

        System.out.println("request->" + sb);
        JSONObject json = new JSONObject(sb.toString());
        String idToken = json.optString("idToken", null);

        System.out.println("LoginServlet doPost--------------> checking idtoken");
        if (idToken == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing idToken\"}");
            return;
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

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Invalid or expired token\"}");
        }
    }
}
