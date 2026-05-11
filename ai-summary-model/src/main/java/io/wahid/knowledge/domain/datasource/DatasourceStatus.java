package io.wahid.knowledge.domain.datasource;

public enum DatasourceStatus {
    CREATED,
    CONNECTING,
    INDEXING,
    READY,
    FAILED,
    DISABLED
}