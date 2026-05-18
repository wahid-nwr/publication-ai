package io.wahid.knowledge.application.core.query.handler;

import io.wahid.knowledge.application.core.query.handler.semantic.SemanticSearchQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.BottomKQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.CompareQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.MaxQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.MinQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.TopKQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.TrendQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.ValueQueryHandler;
import io.wahid.knowledge.domain.query.Query;

import java.util.ArrayList;
import java.util.List;

public class QueryHandlerRegistry {

    private final List<QueryHandler<?>> handlers;

    public QueryHandlerRegistry() {
        this.handlers = new ArrayList<>();
    }

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

    public void register(MaxQueryHandler maxQueryHandler) {
        this.handlers.add(maxQueryHandler);
    }

    public void register(MinQueryHandler minQueryHandler) {
        this.handlers.add(minQueryHandler);
    }

    public void register(TopKQueryHandler topKQueryHandler) {
        this.handlers.add(topKQueryHandler);
    }

    public void register(BottomKQueryHandler bottomKQueryHandler) {
        this.handlers.add(bottomKQueryHandler);
    }

    public void register(CompareQueryHandler compareQueryHandler) {
        this.handlers.add(compareQueryHandler);
    }

    public void register(TrendQueryHandler trendQueryHandler) {
        this.handlers.add(trendQueryHandler);
    }

    public void register(ValueQueryHandler valueQueryHandler) {
        this.handlers.add(valueQueryHandler);
    }

    public void register(SemanticSearchQueryHandler semanticSearchQueryHandler) {
        this.handlers.add(semanticSearchQueryHandler);
    }
}