package io.wahid.knowledge.infrastructure.graph.neo4j.query;

import io.wahid.knowledge.domain.query.result.QueryResult;

public interface QueryService {

    QueryResult query(String tenantId, String workspaceId, String question, int topK) throws Exception;

    String route(String question) throws Exception;
}
