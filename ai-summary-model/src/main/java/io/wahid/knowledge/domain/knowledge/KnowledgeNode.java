package io.wahid.knowledge.domain.knowledge;

import java.util.Map;

public class KnowledgeNode {
    private String id;
    private String workspaceId;
    private NodeType type;
    private String name;
    private Map<String, Object> attributes;
}