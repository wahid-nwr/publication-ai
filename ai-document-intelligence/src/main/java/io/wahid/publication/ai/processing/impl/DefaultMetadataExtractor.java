package io.wahid.publication.ai.processing.impl;

import io.wahid.publication.ai.ingestion.AbstractPipelineStage;
import io.wahid.publication.ai.ingestion.TextChunk;

import java.util.HashMap;
import java.util.Map;

public class DefaultMetadataExtractor extends AbstractPipelineStage {

    @Override
    public void accept(TextChunk chunk) throws Exception {
        Map<String, Object> enriched =
                new HashMap<>(chunk.metadata());

        // Generic metadata
        enriched.putIfAbsent("length", chunk.text().length());
        enriched.putIfAbsent("wordCount",
                chunk.text().split("\\s+").length);

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
        return text.length() < 120 &&
                text.equals(text.toUpperCase());
    }
}
