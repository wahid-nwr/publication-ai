package io.wahid.knowledge.infrastructure.graph.neo4j.query;

import io.wahid.knowledge.domain.query.result.QueryResult;

public interface QueryService {
    /**
     *
     * @param tenantId
     * @param workspaceId
     * @param question
     * @param topK
     * @return
     * @throws Exception
     */
    QueryResult query(String tenantId, String workspaceId, String question, int topK) throws Exception;
}
