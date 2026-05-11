package io.wahid.knowledge.domain.insight;

import java.time.Instant;
import java.util.List;

public class Insight {

    private String id;
    private String tenantId;
    private String workspaceId;
    private InsightType type;
    private String title;
    private String summary;
    private InsightSeverity severity;
    private InsightStatus status;
    private List<InsightEvidence> evidence;
    private Instant createdAt;

    public Insight() {
    }

    // getters/setters
}