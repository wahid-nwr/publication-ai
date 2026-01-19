package io.wahid.publication.ai.infra.qdrant;

public interface QdrantClient {
    boolean collectionExists(String collection);
    int getVectorSize(String collection);
}
