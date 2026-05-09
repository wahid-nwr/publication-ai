package io.wahid.knowledge.infrastructure.vectorstore.qdrant;

import java.util.Map;

public interface QdrantClient {
    boolean isUp();
    boolean collectionExists(String collection);
    int getVectorSize(String collection);
    void upsert(String collection, String pointId, float[] vector, Map<String, Object> payload);
    void deleteAllPoints(String collection);
}
