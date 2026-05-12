package io.wahid.knowledge.infrastructure.embedding.ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.wahid.knowledge.application.core.embedding.EmbeddingClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class OllamaEmbeddingClient implements EmbeddingClient {

    private static final ObjectMapper MAPPER = JsonMapper.builder().build();

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .executor(Executors.newFixedThreadPool(1))
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String baseUrl;
    private final String model;

    public OllamaEmbeddingClient(String baseUrl, String model) {
        this.baseUrl = baseUrl;
        this.model = model;
    }

    @Override
    public int dimension() {
        return 768; // change if using a different model
    }

    @Override
    public float[] embed(String text) {
        return embedBatch(Collections.singletonList(text)).getFirst();
    }

    @Override
    public float[] embed(String text, boolean isAnswer) {
        return embedBatch(Collections.singletonList(text)).getFirst();
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        return embedBatch(texts, false);
    }

    @Override
    public List<float[]> embedBatch(List<String> texts, boolean isAnswer) {
        List<float[]> finalResult = new ArrayList<>();
        if (texts.isEmpty()) return finalResult;

        try {
            // Build JSON payload
            Map<String, Object> payload = Map.of(
                    "model", model,
                    "input", texts
            );

            Instant start = Instant.now();
            String requestBody = MAPPER.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/embeddings"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMinutes(10))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("time taken to send embeddings and get response-> " + Duration.between(start, Instant.now()).toSeconds());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Ollama embedding failed: " + response.body());
            }

            // Parse JSON
            JsonNode root = MAPPER.readTree(response.body());
            JsonNode dataNode = root.path("data");

            if (!dataNode.isArray()) {
                throw new IllegalStateException("Invalid Ollama response: " + response.body());
            }

            List<float[]> result = new ArrayList<>(texts.size());

            for (int i = 0; i < texts.size(); i++) {
                JsonNode item = dataNode.get(i);
                if (item == null || !item.has("embedding")) {
                    result.add(new float[0]);
                    continue;
                }

                JsonNode vecNode = item.get("embedding");
                if (!vecNode.isArray() || vecNode.isEmpty()) {
                    result.add(new float[0]);
                    continue;
                }

                float[] vector = new float[vecNode.size()];
                for (int j = 0; j < vecNode.size(); j++) {
                    vector[j] = vecNode.get(j).floatValue();
                }
                result.add(vector);
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Embedding generation failed", e);
        }
    }
}
