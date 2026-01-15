package io.wahid.publication.ai.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.publication.ai.rag.PromptBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OllamaEmbeddingClient implements EmbeddingClient {

    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final PromptBuilder promptBuilder = new PromptBuilder();

    private final String baseUrl;
    private final String model;

    public OllamaEmbeddingClient(String baseUrl, String model) {
        this.baseUrl = baseUrl;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public int dimension() {
        return 768;
    }
    /* ===================== EMBEDDINGS ===================== */

    @Override
    public List<Float> embed(String text) {
        System.out.println("embedding text->" + text);
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("prompt", text);

            String requestBody = mapper.writeValueAsString(body);

            System.out.println("embedding text to -> " + baseUrl);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/embeddings"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Ollama embedding failed: " + response.body()
                );
            }

            return extractEmbedding(response.body());

        } catch (Exception e) {
            throw new RuntimeException("Embedding generation failed", e);
        }
    }

    private List<Float> extractEmbedding(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        return mapper.convertValue(
                root.path("embedding"),
                mapper.getTypeFactory().constructCollectionType(List.class, Float.class)
        );
    }

    /* ===================== ANSWERS (RAG) ===================== */

    public String answer(String question, String context) {
        System.out.println("answering question ->" + question + ", context->" + context);
        try {
            String prompt = promptBuilder.build(question, context);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("prompt", prompt);
            body.put("stream", false);

            String requestBody = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/generate"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Ollama generation failed: " + response.body()
                );
            }

            return extractAnswer(response.body());

        } catch (Exception e) {
            throw new RuntimeException("LLM answer generation failed", e);
        }
    }

    private String extractAnswer(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        return root.path("response").asText().trim();
    }
}

