package io.wahid.publication.ai.embedding;

import io.wahid.publication.ai.ingestion.TextChunk;
import io.wahid.publication.ai.ingestion.TextChunkConsumer;
import io.wahid.publication.ai.vectorstore.VectorWriter;

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
    public void accept(TextChunk chunk) throws Exception {
        var embedding = embeddingClient.embed(chunk.text());
        vectorWriter.write(chunk, embedding);
    }
}
