package io.wahid.publication.ai.vectorstore;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class QdrantVectorSearcher implements VectorSearcher {

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
    public List<SearchResult> search(List<Float> queryVector, int topK) {
        try {
            String requestBody = buildRequest(queryVector, topK);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            baseUrl + "/collections/" + collection + "/points/search"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Qdrant search failed: " + response.body());
            }

            return parseResults(response.body());

        } catch (Exception e) {
            throw new RuntimeException("Vector search error", e);
        }
    }

    private String buildRequest(List<Float> vector, int topK) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("vector", vector);
        body.put("limit", topK);
        body.put("with_payload", true);
        body.put("with_vector", false);

        return mapper.writeValueAsString(body);
    }

    private List<SearchResult> parseResults(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        JsonNode result = root.get("result");

        List<SearchResult> results = new ArrayList<>();

        for (JsonNode node : result) {
            JsonNode payload = node.get("payload");

            results.add(new SearchResult(
                    payload.get("documentId").asText(),
                    payload.get("text").asText(),
                    mapper.convertValue(payload, Map.class),
                    node.get("score").asDouble()
            ));
        }
        return results;
    }
}
