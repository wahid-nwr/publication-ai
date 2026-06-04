package io.wahid.knowledge.domain.workspace;

import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> TENANT = new ThreadLocal<>();

    public static void set(UUID tenantId) {
        TENANT.set(tenantId);
    }

    public static UUID get() {
        return TENANT.get();
    }

    public static void clear() {
        TENANT.remove();
    }
}