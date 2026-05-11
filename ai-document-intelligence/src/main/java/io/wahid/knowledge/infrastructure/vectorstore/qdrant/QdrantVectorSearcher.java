package io.wahid.knowledge.infrastructure.vectorstore.qdrant;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.knowledge.infrastructure.vectorstore.dto.DocumentPayload;
import io.wahid.knowledge.application.retrieval.vector.VectorSearcher;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QdrantVectorSearcher implements VectorSearcher {

    private static final Logger LOGGER = Logger.getLogger(QdrantVectorSearcher.class.getName());
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    private final String baseUrl;
    private final String collection;

    public QdrantVectorSearcher(String baseUrl, String collection) {
        this.baseUrl = baseUrl;
        this.collection = collection;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public List<SearchResult> search(float[] queryVector, int topK) {
        try {
            String requestBody = buildRequest(queryVector, topK);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/collections/" + collection + "/points/search"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Qdrant search failed: " + response.body());
            }

            return parseResults(response.body());

        } catch (Exception e) {
            throw new RuntimeException("Vector search error", e);
        }
    }

    private String buildRequest(float[] vector, int topK) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("vector", vector);
        body.put("limit", topK);
        body.put("with_payload", true);
        body.put("with_vector", false);

        return mapper.writeValueAsString(body);
    }

    private List<SearchResult> parseResults(String json) throws Exception {
        LOGGER.log(Level.INFO, "Search result json: {0}", json);
        JsonNode root = mapper.readTree(json);
        JsonNode result = root.get("result");

        List<SearchResult> results = new ArrayList<>();

        if (result == null || !result.isArray()) {
            return results;
        }

        for (JsonNode node : result) {

            JsonNode payload = node.get("payload");
            if (payload == null || payload.isNull()) {
                LOGGER.log(Level.WARNING, "Qdrant result without payload: {0}", node);
                continue; // skip broken entry
            }

            JsonNode textNode = payload.get("text");
            if (textNode == null || textNode.isNull()) {
                continue;
            }

            JsonNode documentIdNode = payload.get("documentId");
            String documentId = documentIdNode != null && !documentIdNode.isNull()
                    ? documentIdNode.asText()
                    : "unknown";

            double score = node.has("score") ? node.get("score").asDouble() : 0.0;

            results.add(new SearchResult(
                    documentId,
                    textNode.asText(),
                    mapper.convertValue(payload, DocumentPayload.class),
                    score
            ));
        }

        return results;
    }

}
