package io.wahid.knowledge.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.knowledge.ApplicationContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenAIEmbeddingClient implements EmbeddingClient {

    private static final Logger LOGGER = Logger.getLogger(OpenAIEmbeddingClient.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private static final String ENDPOINT = "https://api.openai.com/v1/embeddings";
    private static final String MODEL = "text-embedding-3-small";
    private static final int DIMENSION = 1536;

    private final String apiKey;

    public OpenAIEmbeddingClient(String apiKey) {
        this.apiKey = apiKey;
    }

    // --------------------------------------------------------
    // Interface methods
    // --------------------------------------------------------

    @Override
    public int dimension() {
        return DIMENSION;
    }

    @Override
    public float[] embed(String text) {
        List<float[]> result = embedBatch(List.of(text));
        return result.isEmpty() ? new float[0] : result.getFirst();
    }

    @Override
    public float[] embed(String text, boolean isAnswer) {
        List<float[]> result = embedBatch(List.of(text));
        return result.isEmpty() ? new float[0] : result.getFirst();
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        return embedBatch(texts, false);
    }

    @Override
    public List<float[]> embedBatch(List<String> texts, boolean isAnswer) {
        if (texts == null || texts.isEmpty()) {
            return List.of();
        }

        ApplicationContext.setMetricValue("node", texts.size());
        try {
            Map<String, Object> payload = Map.of(
                    "model", MODEL,
                    "input", texts
            );

            String body = MAPPER.writeValueAsString(payload);
            LOGGER.log(Level.INFO, "embedding batch body to openai -> {0}", body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "OpenAI embedding failed (" + response.statusCode() + "): " + response.body()
                );
            }

            JsonNode root = MAPPER.readTree(response.body());
            JsonNode data = root.get("data");

            LOGGER.log(Level.FINEST, "embedding response from openai -> {0}", data);
            if (data == null || !data.isArray()) {
                throw new IllegalStateException("Invalid OpenAI response: " + response.body());
            }

            List<float[]> vectors = new ArrayList<>(data.size());

            for (JsonNode item : data) {
                JsonNode emb = item.get("embedding");
                if (emb == null || !emb.isArray()) {
                    vectors.add(new float[0]);
                    continue;
                }

                float[] vec = new float[emb.size()];
                for (int i = 0; i < emb.size(); i++) {
                    vec[i] = emb.get(i).floatValue();
                }
                ApplicationContext.setMetricValue("vector", vec.length);
                vectors.add(vec);
            }

            return vectors;

        } catch (Exception e) {
            throw new RuntimeException("Embedding generation failed", e);
        }
    }
}
