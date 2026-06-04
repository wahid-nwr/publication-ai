package io.wahid.knowledge.domain.workspace;

import io.wahid.knowledge.domain.shared.BaseEntity;

public class Tenant extends BaseEntity {
    private String name;
    private TenantStatus status;

    public Tenant() {
    }

    // getters/setters
}