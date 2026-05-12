package io.wahid.knowledge.domain.knowledge;

import io.wahid.knowledge.domain.shared.BaseEntity;

import java.util.Map;

public class KnowledgeNode extends BaseEntity {
    private String id;
    private String workspaceId;
    private NodeType type;
    private String name;
    private Map<String, Object> attributes;
}