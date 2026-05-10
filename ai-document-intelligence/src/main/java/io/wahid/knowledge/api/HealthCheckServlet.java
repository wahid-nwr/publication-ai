package io.wahid.knowledge.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.wahid.knowledge.infrastructure.dto.HealthStatus;
import io.wahid.knowledge.infrastructure.health.HealthService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class HealthCheckServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HealthService healthService;

    public HealthCheckServlet(HealthService healthService) {
        this.healthService = healthService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HealthStatus status = healthService.check();

        ObjectNode components = objectMapper.createObjectNode();
        components.put("ollama", status.ollamaUp() ? "UP" : "DOWN");
        components.put("qdrant", status.qdrantUp() ? "UP" : "DOWN");

        ObjectNode root = objectMapper.createObjectNode();
        root.put("status", status.isHealthy() ? "UP" : "DOWN");
        root.set("components", components);

        resp.setStatus(
                status.isHealthy()
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
}
