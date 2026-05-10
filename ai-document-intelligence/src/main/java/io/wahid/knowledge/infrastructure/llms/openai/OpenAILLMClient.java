package io.wahid.knowledge.infrastructure.llms.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.knowledge.infrastructure.llms.LLMClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OpenAILLMClient implements LLMClient {

    private static final String OPENAI_URL = "https://api.openai.com/v1/responses";

    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey;
    private final String model;

    public OpenAILLMClient(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String generate(String prompt) throws Exception {

        String body = mapper.writeValueAsString(
                mapper.createObjectNode()
                        .put("model", model)
                        .put("input", prompt)
                        .put("temperature", 0.2)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "OpenAI error: " + response.statusCode() + " -> " + response.body()
            );
        }

        return extractText(response.body());
    }

    @Override
    public boolean isUp() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_URL + "/api/tags"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            return httpClient.send(req, HttpResponse.BodyHandlers.discarding())
                    .statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasModel(String modelName) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_URL + "/api/tags"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> resp =
                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(resp.body());
            for (JsonNode model : root.path("models")) {
                if (model.path("name").asText().startsWith(modelName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String extractText(String json) throws Exception {
        JsonNode root = mapper.readTree(json);

        // Responses API output format
        return root
                .path("output")
                .get(0)
                .path("content")
                .get(0)
                .path("text")
                .asText();
    }
}

