package io.wahid.knowledge.application.core.pipeline.impl;

import io.wahid.knowledge.application.core.pipeline.AbstractPipelineStage;
import io.wahid.knowledge.application.core.ingestion.chunking.dto.TextChunk;

import java.text.Normalizer;

public class DefaultTextNormalizer extends AbstractPipelineStage {

    @Override
    public void accept(TextChunk chunk) throws Exception {
        String normalized = normalize(chunk.text());

        if (normalized.isBlank()) return;

        downstream.accept(new TextChunk(
                chunk.documentId(),
                chunk.tenantId(),
                chunk.workspaceId(),
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
