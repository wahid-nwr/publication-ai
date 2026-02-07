package io.wahid.publication.ai.dto;

import java.util.Map;

public record TextChunk(
        String documentId,
        String text,
        Map<String, Object> metadata
) {}
