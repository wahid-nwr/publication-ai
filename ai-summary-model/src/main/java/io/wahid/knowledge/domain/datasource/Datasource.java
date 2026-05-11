package io.wahid.knowledge.domain.datasource;

import java.time.Instant;

public class Datasource {

    private String id;
    private String tenantId;
    private String workspaceId;
    private String name;
    private DatasourceType type;
    private DatasourceStatus status;
    private DatasourceConfig config;
    private Instant createdAt;
    private Instant updatedAt;
    public Datasource() {
    }

    // getters/setters
}