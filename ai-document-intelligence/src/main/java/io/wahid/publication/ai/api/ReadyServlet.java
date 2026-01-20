package io.wahid.publication.ai.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.wahid.publication.ai.config.AppConfig;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.infra.ollama.OllamaClient;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ReadyServlet extends HttpServlet {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final OllamaClient ollamaClient;
    private final QdrantClient qdrantClient;
    private final EmbeddingClient embeddingClient;

    public ReadyServlet(OllamaClient ollamaClient,
                        EmbeddingClient embeddingClient,
                        QdrantClient qdrantClient) {
        this.ollamaClient = ollamaClient;
        this.qdrantClient = qdrantClient;
        this.embeddingClient = embeddingClient;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ObjectNode checks = MAPPER.createObjectNode();

        boolean ollamaUp;
        try {
            ollamaUp = ollamaClient.isUp();
        } catch (Exception e) {
            ollamaUp = false;
        }
        checks.put("ollama", ollamaUp ? "UP" : "DOWN");

        boolean hasModel;
        try {
            hasModel = ollamaClient.hasModel(AppConfig.embeddingModel());
        } catch (Exception e) {
            hasModel = false;
        }

        boolean embeddingOk = ollamaUp && hasModel;
        checks.put("embeddingModel", embeddingOk ? "UP" : "MISSING");

        boolean llmOk = ollamaUp && hasModel;
        checks.put("llmModel", llmOk ? "UP" : "MISSING");

        boolean qdrantUp;
        try {
            qdrantUp = qdrantClient.collectionExists(AppConfig.collectionName());
        } catch (Exception e) {
            qdrantUp = false;
        }
        checks.put("qdrant", qdrantUp ? "UP" : "DOWN");

        boolean vectorSizeMatch = false;
        if (qdrantUp) {
            int actual = qdrantClient.getVectorSize(AppConfig.collectionName());
            vectorSizeMatch = actual == embeddingClient.dimension();
        }
        checks.put("vectorSizeMatch", vectorSizeMatch);

        boolean ready = ollamaUp
                && llmOk
                && qdrantUp
                && vectorSizeMatch;

        ObjectNode root = MAPPER.createObjectNode();
        root.put("ready", ready);
        root.set("checks", checks);

        resp.setStatus(
                ready ? HttpServletResponse.SC_OK
                        : HttpServletResponse.SC_SERVICE_UNAVAILABLE
        );
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root));
    }
}
