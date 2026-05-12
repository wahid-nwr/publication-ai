package io.wahid.knowledge.application.core.pipeline.impl;

import io.wahid.knowledge.application.core.pipeline.AbstractPipelineStage;
import io.wahid.knowledge.application.core.ingestion.chunking.dto.TextChunk;

import java.util.HashMap;
import java.util.Map;

public class DefaultMetadataExtractor extends AbstractPipelineStage {

    @Override
    public void accept(TextChunk chunk) throws Exception {
        Map<String, Object> enriched = new HashMap<>(chunk.metadata());

        // Generic metadata
        enriched.putIfAbsent("length", chunk.text().length());
        enriched.putIfAbsent("wordCount", chunk.text().split("\\s+").length);

        // Heuristic: title detection
        if (isLikelyHeader(chunk.text())) {
            enriched.put("isHeader", true);
        }

        downstream.accept(new TextChunk(
                chunk.documentId(),
                chunk.text(),
                enriched
        ));
    }

    private boolean isLikelyHeader(String text) {
        return text.length() < 120 && text.equals(text.toUpperCase());
    }
}
