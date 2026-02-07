package io.wahid.publication.ai.vectorstore;

import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.ingestion.AbstractPipelineStage;
import io.wahid.publication.ai.dto.TextChunk;
import io.wahid.publication.ai.ingestion.TextChunkConsumer;

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
