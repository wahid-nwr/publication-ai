package io.wahid.knowledge.domain.workspace;

import io.wahid.knowledge.domain.shared.BaseEntity;

import java.time.Instant;

public class Workspace extends BaseEntity {
    private String tenantId;
    private String name;
    private String description;
    private WorkspaceStatus status;

    public Workspace() {
    }

    // getters/setters
}