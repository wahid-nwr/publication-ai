package io.wahid.knowledge.vectorstore;

import io.wahid.knowledge.dto.TextChunk;

import java.io.IOException;
import java.util.Map;

public interface VectorWriter {

    int requiredVectorSize() throws IOException, InterruptedException;
    void write(TextChunk chunk, float[] embedding) throws Exception;
    void write(float[] floats, Map<String, Object> metadata);
}
