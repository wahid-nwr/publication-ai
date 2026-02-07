package io.wahid.publication.ai.vectorstore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.publication.ai.dto.TextChunk;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QdrantVectorWriter implements VectorWriter {
    private static final Logger LOGGER = Logger.getLogger(QdrantVectorWriter.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final URI upsertUri;
    private HttpClient httpClient;
    private String baseUrl;
    private String collection;

    public QdrantVectorWriter(String baseUrl, String collection) {
        this.baseUrl = baseUrl;
        this.collection = collection;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.upsertUri = URI.create(
                baseUrl + "/collections/" + collection + "/points?wait=true"
        );
    }

    @Override
    public int requiredVectorSize() throws IOException, InterruptedException {
        return getCollectionVectorSize(collection);
    }

    @Override
    public void write(TextChunk chunk, float[] embedding) throws Exception {

        Map<String, Object> payload = new HashMap<>(chunk.metadata());
        payload.put("documentId", chunk.documentId());
        payload.put("text", chunk.text());
        LOGGER.log(Level.INFO, "writing vectors -> {0}", chunk.text());
        Map<String, Object> point = Map.of(
                "id", UUID.randomUUID().toString(),
                "vector", embedding,
                "payload", payload
        );

        Map<String, Object> body = Map.of(
                "points", new Object[]{point}
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(upsertUri)
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(
                        MAPPER.writeValueAsString(body)
                ))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "Qdrant write failed: " + response.statusCode() + " " + response.body()
            );
        }
    }

    @Override
    public void write(float[] floats, Map<String, Object> metadata) {

    }

    public int getCollectionVectorSize(String collection) throws IOException, InterruptedException {
        HttpResponse<String> res = httpClient.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/collections/" + collection))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        if (res.statusCode() != 200) {
            throw new IllegalStateException("Failed to fetch collection config");
        }

        JsonNode root = MAPPER.readTree(res.body());
        return root
                .path("result")
                .path("config")
                .path("params")
                .path("vectors")
                .path("size")
                .asInt();
    }
}
