package io.wahid.knowledge.application.ingestion.chunking;

import io.wahid.knowledge.application.ingestion.chunking.dto.TextChunk;

public interface TextChunkConsumer {

    void accept(TextChunk chunk) throws Exception;

}
