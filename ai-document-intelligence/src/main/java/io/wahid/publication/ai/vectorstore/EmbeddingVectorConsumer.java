package io.wahid.publication.ai.vectorstore;

import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.ingestion.TextChunk;
import io.wahid.publication.ai.ingestion.TextChunkConsumer;

import java.util.List;

public class EmbeddingVectorConsumer implements TextChunkConsumer {

    private final EmbeddingClient embeddingClient;
    private final VectorWriter vectorWriter;

    public EmbeddingVectorConsumer(
            EmbeddingClient embeddingClient,
            VectorWriter vectorWriter
    ) {
        this.embeddingClient = embeddingClient;
        this.vectorWriter = vectorWriter;
    }

    @Override
    public void accept(TextChunk chunk) {
        try {
            List<Float> embedding = embeddingClient.embed(chunk.text());
            vectorWriter.write(chunk, embedding);
        } catch (Exception e) {
            throw new RuntimeException("Embedding pipeline failed", e);
        }
    }
}
