package io.wahid.knowledge.application.core.query.handler;

import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.result.QueryResult;

public interface QueryHandler<Q extends Query> {
    boolean supports(Q query);

    QueryResult handle(Q query) throws Exception;
}