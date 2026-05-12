package io.wahid.knowledge.domain.document;

import io.wahid.knowledge.domain.shared.BaseEntity;

public class Document extends BaseEntity {
    private String tenantId;
    private String workspaceId;
    private String datasourceId;
    private String title;
    private DocumentType type;
    private DocumentMetadata metadata;
}