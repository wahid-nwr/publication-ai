package io.wahid.knowledge.application.core.ingestion.chunking.dto;

import java.util.Map;
import java.util.UUID;

public record TextChunk(
        String documentId,
        String tenantId,
        String workspaceId,
        String text,
        Map<String, Object> metadata
) {}
