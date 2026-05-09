package io.wahid.knowledge.vectorstore;

import io.wahid.knowledge.embedding.EmbeddingClient;
import io.wahid.knowledge.ingestion.AbstractPipelineStage;
import io.wahid.knowledge.dto.TextChunk;
import io.wahid.knowledge.ingestion.TextChunkConsumer;

public class EmbeddingVectorConsumer extends AbstractPipelineStage implements TextChunkConsumer {

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
    public void accept(TextChunk chunk) throws Exception {
        System.out.println("EmbeddingVectorConsumer received chunk: "
                + chunk.text().substring(0, Math.min(80, chunk.text().length())));

        // 🔥 THIS must be called
        float[] vector = embeddingClient.embed(chunk.text());

        if (vector == null || vector.length == 0) {
            System.out.println("⚠️ Empty embedding, skipping");
            return;
        }

        vectorWriter.write(
                chunk,
                vector
        );
    }
}
