package io.wahid.publication.ai.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.wahid.publication.ai.config.AppConfig;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.infra.ollama.OllamaClient;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class InfoServlet extends HttpServlet {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final EmbeddingClient embeddingClient;

    public InfoServlet(EmbeddingClient embeddingClient) {
        this.embeddingClient = embeddingClient;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        ObjectNode root = MAPPER.createObjectNode();

        // Basic info
        root.put("name", "Java RAG Platform");
        root.put("version", "1.0.0");
        root.put("description", "Retrieval-Augmented Generation platform built in pure Java");

        // Models
        ObjectNode models = root.putObject("models");
        models.put("embedding", AppConfig.embeddingModel());
        models.put("llm", AppConfig.llmModel());

        // Vector store
        ObjectNode vectorStore = root.putObject("vectorStore");
        vectorStore.put("type", "qdrant");
        vectorStore.put("collection", AppConfig.collectionName());
        vectorStore.put("vectorSize", embeddingClient.dimension());

        // Capabilities
        ArrayNode capabilities = root.putArray("capabilities");
        capabilities.add("document-ingestion");
        capabilities.add("semantic-search");
        capabilities.add("rag-question-answering");

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root));
    }
}
