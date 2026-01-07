package io.wahid.publication.ai.vectorstore;

import io.wahid.publication.ai.ingestion.TextChunk;

import java.util.List;

public interface VectorWriter {

    void write(TextChunk chunk, List<Float> embedding) throws Exception;

}
