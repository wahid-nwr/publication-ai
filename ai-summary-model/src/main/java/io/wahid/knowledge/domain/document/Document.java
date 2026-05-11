package io.wahid.knowledge.domain.document;

import java.time.Instant;

public class Document {
    private String id;
    private String workspaceId;
    private String datasourceId;
    private String title;
    private String contentType;
    private Instant createdAt;
}