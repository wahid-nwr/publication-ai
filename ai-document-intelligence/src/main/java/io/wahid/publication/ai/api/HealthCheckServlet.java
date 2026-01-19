package io.wahid.publication.ai.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HealthCheckServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        boolean ollamaUp = checkOllama();
        boolean qdrantUp = checkQdrant();

        boolean healthy = ollamaUp && qdrantUp;

        ObjectNode components = objectMapper.createObjectNode();
        components.put("ollama", ollamaUp ? "UP" : "DOWN");
        components.put("qdrant", qdrantUp ? "UP" : "DOWN");

        ObjectNode root = objectMapper.createObjectNode();
        root.put("status", healthy ? "UP" : "DOWN");
        root.set("components", components);

        resp.setStatus(
                healthy
                        ? HttpServletResponse.SC_OK
                        : HttpServletResponse.SC_SERVICE_UNAVAILABLE
        );
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            resp.getWriter().write(objectMapper.writeValueAsString(root));
        } catch (JsonProcessingException ex) {
            log(ex.getMessage());
        }
    }

    private boolean checkOllama() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("http://ollama:11434/api/tags").openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(500);
            conn.setReadTimeout(500);

            int status = conn.getResponseCode();
            return status == 200;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkQdrant() {
        // TODO handle deprecated URL http://localhost:6333/collections/documents USE FOR READYNESS
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("http://qdrant:6333/healthz").openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(500);
            conn.setReadTimeout(500);

            int status = conn.getResponseCode();
            return status == 200;

        } catch (Exception e) {
            return false;
        }
    }
}
