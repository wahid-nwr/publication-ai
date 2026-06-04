package io.wahid.knowledge.domain.workspace;

import java.util.UUID;

public interface TenantOwned {
    UUID getTenantId();
}