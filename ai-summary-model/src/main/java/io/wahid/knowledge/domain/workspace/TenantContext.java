package io.wahid.knowledge.domain.workspace;

import java.util.UUID;

public final class TenantContext {

    private UUID TENANT;

    public void set(UUID tenantId) {
        this.TENANT = tenantId;
    }

    public UUID get() {
        return TENANT;
    }

    public void clear() {
        this.TENANT = null;
    }
}