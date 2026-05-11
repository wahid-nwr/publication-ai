package io.wahid.knowledge.domain.knowledge;

import java.util.List;

public interface KnowledgeRepository {
    void saveNode(KnowledgeNode node);
    List<KnowledgeNode> findRelated(String nodeId);
}