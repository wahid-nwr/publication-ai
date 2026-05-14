package io.wahid.knowledge.application.core.query.handler;

import io.wahid.knowledge.domain.query.Query;

import java.util.List;

public class QueryHandlerRegistry {

    private final List<QueryHandler<?>> handlers;

    public QueryHandlerRegistry(List<QueryHandler<?>> handlers) {
        this.handlers = handlers;
    }

    @SuppressWarnings("unchecked")
    public <Q extends Query> QueryHandler<Q> resolve(Q query) {

        return (QueryHandler<Q>) handlers.stream()
                .filter(h -> h.supports(query))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No handler found for query type: "
                                        + query.getType()
                        ));
    }
}