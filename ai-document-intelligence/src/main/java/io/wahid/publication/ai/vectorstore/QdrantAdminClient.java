package io.wahid.publication.ai.vectorstore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class QdrantAdminClient implements QdrantClient {

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl;

    public QdrantAdminClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean isUp() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("http://ollama:11434/api/tags").openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(500);
            conn.setReadTimeout(500);

            int status = conn.getResponseCode();
            return status == 200;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean collectionExists(String collection) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/collections/" + collection))
                    .GET()
                    .build();

            return client.send(req, HttpResponse.BodyHandlers.discarding()).statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getVectorSize(String collection) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/collections/" + collection))
                    .GET()
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(resp.body());
            return root.path("result")
                    .path("config")
                    .path("params")
                    .path("vectors")
                    .path("size")
                    .asInt(-1);
        } catch (Exception e) {
            return -1;
        }
    }

    public void ensureCollection(
            String collection,
            int vectorSize,
            String distance
    ) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/collections/" + collection))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString("""
                            {
                              "vectors": {
                                "size": %d,
                                "distance": "%s"
                              }
                            }
                            """.formatted(vectorSize, distance)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // ✅ Treat collection exists as success
            if (response.statusCode() == 200 || response.body().contains("already exists")) {
                return; // all good
            }

            if (response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "Failed to create Qdrant collection: " + response.body()
                );
            }

        } catch (java.net.ConnectException e) {
            throw new RuntimeException("Qdrant not reachable", e);
        } catch (Exception e) {
            throw new RuntimeException("Qdrant admin call failed", e);
        }
    }

    @Override
    public void upsert(
            String collection,
            String pointId,
            float[] vector,
            Map<String, Object> payload
    ) {
        try {
            String body = buildRequestBody(pointId, vector, payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/collections/" + collection + "/points"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                throw new RuntimeException("Qdrant upsert failed: " + response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upsert into Qdrant", e);
        }
    }

    private String buildRequestBody(
            String pointId,
            float[] vector,
            Map<String, Object> payload
    ) throws Exception {
        ObjectNode point = mapper.createObjectNode();
        point.put("id", pointId);
        point.set("vector", mapper.valueToTree(vector));
        point.set("payload", mapper.valueToTree(payload));

        ObjectNode root = mapper.createObjectNode();
        root.set("points", mapper.createArrayNode().add(point));

        return mapper.writeValueAsString(root);
    }
}
