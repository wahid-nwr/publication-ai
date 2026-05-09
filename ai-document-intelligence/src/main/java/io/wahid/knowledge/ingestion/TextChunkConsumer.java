package io.wahid.knowledge.ingestion;

import io.wahid.knowledge.dto.TextChunk;

public interface TextChunkConsumer {

    void accept(TextChunk chunk) throws Exception;

}
