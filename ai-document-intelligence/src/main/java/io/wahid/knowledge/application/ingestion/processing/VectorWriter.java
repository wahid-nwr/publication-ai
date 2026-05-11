package io.wahid.knowledge.application.ingestion.processing;

import io.wahid.knowledge.application.ingestion.chunking.dto.TextChunk;

import java.io.IOException;
import java.util.Map;

public interface VectorWriter {
    int requiredVectorSize() throws IOException, InterruptedException;
    void write(TextChunk chunk, float[] embedding) throws Exception;
    void write(float[] floats, Map<String, Object> metadata);
}
