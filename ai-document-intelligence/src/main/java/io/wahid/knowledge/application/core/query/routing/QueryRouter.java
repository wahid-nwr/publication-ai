package io.wahid.knowledge.application.core.query.routing;

import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.QueryRequest;
import io.wahid.knowledge.domain.query.result.QueryResult;

public interface QueryRouter {
    QueryResult route(Query query) throws Exception;
}