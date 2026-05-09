package io.wahid.knowledge.processing.impl;

import io.wahid.knowledge.ingestion.AbstractPipelineStage;
import io.wahid.knowledge.dto.TextChunk;

import java.text.Normalizer;

public class DefaultTextNormalizer extends AbstractPipelineStage {

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
