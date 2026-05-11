package io.wahid.knowledge.domain.workspace;

import java.time.Instant;

public class Workspace {
    private String id;
    private String tenantId;
    private String name;
    private String description;
    private WorkspaceStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public Workspace() {
    }

    // getters/setters
}