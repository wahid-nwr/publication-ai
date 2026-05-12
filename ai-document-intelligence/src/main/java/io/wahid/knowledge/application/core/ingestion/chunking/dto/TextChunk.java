package io.wahid.knowledge.application.core.ingestion.chunking.dto;

import java.util.Map;

public record TextChunk(
        String documentId,
        String text,
        Map<String, Object> metadata
) {}
