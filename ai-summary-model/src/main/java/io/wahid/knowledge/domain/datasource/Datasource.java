package io.wahid.knowledge.domain.datasource;

import io.wahid.knowledge.domain.shared.BaseEntity;

import java.time.Instant;

public class Datasource extends BaseEntity {
    private String tenantId;
    private String workspaceId;
    private String name;
    private DatasourceType type;
    private DatasourceStatus status;
    private DatasourceConfig config;
    public Datasource() {
    }

    // getters/setters
}