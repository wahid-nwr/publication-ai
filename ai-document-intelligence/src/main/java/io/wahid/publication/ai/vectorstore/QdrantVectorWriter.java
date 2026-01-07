package io.wahid.publication.ai.vectorstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.publication.ai.ingestion.TextChunk;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QdrantVectorWriter implements VectorWriter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient httpClient;
    private final URI upsertUri;

    public QdrantVectorWriter(String baseUrl, String collection) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.upsertUri = URI.create(
                baseUrl + "/collections/" + collection + "/points?wait=true"
        );
    }

    @Override
    public void write(TextChunk chunk, List<Float> embedding) throws Exception {

        Map<String, Object> payload = new HashMap<>(chunk.metadata());
        payload.put("documentId", chunk.documentId());
        payload.put("text", chunk.text());

        Map<String, Object> point = Map.of(
                "id", UUID.randomUUID().toString(),
                "vector", embedding,
                "payload", payload
        );

        Map<String, Object> body = Map.of(
                "points", new Object[]{ point }
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
}
