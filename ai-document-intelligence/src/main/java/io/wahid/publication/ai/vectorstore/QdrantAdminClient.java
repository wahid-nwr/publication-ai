package io.wahid.publication.ai.vectorstore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class QdrantAdminClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl;

    public QdrantAdminClient(String baseUrl) {
        this.baseUrl = baseUrl;
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

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

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
}
