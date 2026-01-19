package io.wahid.publication.ai.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.wahid.publication.ai.infra.ollama.OllamaClient;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ReadyServlet extends HttpServlet {

    private static final String EMBEDDING_MODEL = "nomic-embed-text";
    private static final String LLM_MODEL = "llama3";
    private static final String COLLECTION = "documents";
    private static final int EXPECTED_VECTOR_SIZE = 768;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final OllamaClient ollamaClient;
    private final QdrantClient qdrantClient;

    public ReadyServlet(OllamaClient ollamaClient,
                        QdrantClient qdrantClient) {
        this.ollamaClient = ollamaClient;
        this.qdrantClient = qdrantClient;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        ObjectNode checks = MAPPER.createObjectNode();

        boolean ollamaUp = ollamaClient.isUp();
        checks.put("ollama", ollamaUp ? "UP" : "DOWN");

        boolean embeddingOk = ollamaUp && ollamaClient.hasModel(EMBEDDING_MODEL);
        checks.put("embeddingModel", embeddingOk ? "UP" : "MISSING");

        boolean llmOk = ollamaUp && ollamaClient.hasModel(LLM_MODEL);
        checks.put("llmModel", llmOk ? "UP" : "MISSING");

        boolean qdrantUp = qdrantClient.collectionExists(COLLECTION);
        checks.put("qdrant", qdrantUp ? "UP" : "DOWN");

        boolean vectorSizeMatch = false;
        if (qdrantUp) {
            int actual = qdrantClient.getVectorSize(COLLECTION);
            vectorSizeMatch = actual == EXPECTED_VECTOR_SIZE;
        }
        checks.put("vectorSizeMatch", vectorSizeMatch);

        boolean ready = ollamaUp
                && embeddingOk
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
