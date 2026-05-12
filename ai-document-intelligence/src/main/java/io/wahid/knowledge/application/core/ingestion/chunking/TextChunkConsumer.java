package io.wahid.knowledge.application.core.ingestion.chunking;

import io.wahid.knowledge.application.core.ingestion.chunking.dto.TextChunk;

public interface TextChunkConsumer {

    void accept(TextChunk chunk) throws Exception;

}
