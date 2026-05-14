package io.wahid.knowledge.application.core.query.engine;

import io.wahid.knowledge.application.core.query.handler.QueryHandler;
import io.wahid.knowledge.application.core.query.handler.QueryHandlerRegistry;
import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.result.QueryResult;

public class DefaultQueryEngine implements QueryEngine {

    private final QueryHandlerRegistry registry;

    public DefaultQueryEngine(QueryHandlerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public QueryResult execute(Query query) throws Exception {

        QueryHandler<Query> handler = registry.resolve(query);

        return handler.handle(query);
    }
}