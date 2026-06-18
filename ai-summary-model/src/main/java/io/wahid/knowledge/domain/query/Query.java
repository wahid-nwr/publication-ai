package io.wahid.knowledge.domain.query;

import io.wahid.knowledge.domain.shared.BaseEntity;

public class Query extends BaseEntity {

    private final String tenantId;
    private final String workspaceId;
    private final String text;
    private final QueryIntent intent;
    private final QueryType type;
    private final QueryContext context;

    public Query() {
        this.tenantId = null;
        this.workspaceId = null;
        this.text = null;
        this.intent = null;
        this.type = null;
        this.context = null;
    }

    public Query(String tenantId, String workspaceId, String text, QueryIntent intent, QueryType type,
                 QueryContext context) {
        this.tenantId = tenantId;
        this.workspaceId = workspaceId;
        this.text = text;
        this.intent = intent;
        this.type = type;
        this.context = context;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getText() {
        return text;
    }

    public QueryIntent getIntent() {
        return intent;
    }

    public QueryType getType() {
        return type;
    }

    public QueryContext getContext() {
        return context;
    }
}