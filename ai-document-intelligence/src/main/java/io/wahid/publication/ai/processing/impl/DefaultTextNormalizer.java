package io.wahid.publication.ai.processing.impl;

import io.wahid.publication.ai.ingestion.TextChunk;
import io.wahid.publication.ai.ingestion.TextChunkConsumer;
import io.wahid.publication.ai.processing.TextNormalizer;

import java.text.Normalizer;

public class DefaultTextNormalizer implements TextNormalizer {

    private TextChunkConsumer downstream;

    @Override
    public void setDownstream(TextChunkConsumer downstream) {
        this.downstream = downstream;
    }

    @Override
    public void accept(TextChunk chunk) throws Exception {
        String normalized = normalize(chunk.text());

        if (normalized.isBlank()) return;

        downstream.accept(new TextChunk(
                chunk.documentId(),
                normalized,
                chunk.metadata()
        ));
    }

    private String normalize(String text) {
        String s = Normalizer.normalize(text, Normalizer.Form.NFKC);
        s = s.replaceAll("\\p{Cntrl}", "");
        s = s.replaceAll("\\s+", " ");
        return s.trim();
    }
}
