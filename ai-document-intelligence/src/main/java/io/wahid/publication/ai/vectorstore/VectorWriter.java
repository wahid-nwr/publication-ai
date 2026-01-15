package io.wahid.publication.ai.vectorstore;

import io.wahid.publication.ai.ingestion.TextChunk;

import java.io.IOException;
import java.util.List;

public interface VectorWriter {

    int requiredVectorSize() throws IOException, InterruptedException;
    void write(TextChunk chunk, List<Float> embedding) throws Exception;

}
