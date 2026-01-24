package io.wahid.publication.ai.vectorstore;

import io.wahid.publication.ai.ingestion.TextChunk;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface VectorWriter {

    int requiredVectorSize() throws IOException, InterruptedException;
    void write(TextChunk chunk, float[] embedding) throws Exception;
    void write(float[] floats, Map<String, Object> metadata);
}
