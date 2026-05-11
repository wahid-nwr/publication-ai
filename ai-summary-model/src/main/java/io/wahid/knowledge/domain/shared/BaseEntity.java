package io.wahid.knowledge.domain.shared;

import java.time.Instant;

public abstract class BaseEntity {
    protected String id;
    protected Instant createdAt;
    protected Instant updatedAt;
}