package io.wahid.knowledge.domain.document;

import io.wahid.knowledge.domain.shared.BaseEntity;

import java.util.UUID;

public class Document extends BaseEntity {
    private UUID tenantId;
    private UUID workspaceId;
    private String datasourceId;
    private String title;
    private DocumentType type;
    private DocumentMetadata metadata;
}