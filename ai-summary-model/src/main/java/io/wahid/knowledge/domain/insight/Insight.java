package io.wahid.knowledge.domain.insight;

import io.wahid.knowledge.domain.shared.BaseEntity;

import java.util.List;

public class Insight extends BaseEntity {
    private String tenantId;
    private String workspaceId;
    private InsightType type;
    private String title;
    private String summary;
    private InsightSeverity severity;
    private InsightStatus status;
    private List<InsightEvidence> evidence;

    public Insight() {
    }

    // getters/setters
}