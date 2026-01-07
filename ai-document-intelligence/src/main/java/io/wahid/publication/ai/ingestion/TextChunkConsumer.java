package io.wahid.publication.ai.ingestion;

public interface TextChunkConsumer {

    void accept(TextChunk chunk) throws Exception;

}
