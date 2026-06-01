package io.wahid.knowledge.application.core.query.handler;

import io.wahid.knowledge.application.core.query.NumericIntentParser;
import io.wahid.knowledge.application.core.query.handler.semantic.SemanticSearchQueryHandler;
import io.wahid.knowledge.application.core.query.model.SemanticQuery;
import io.wahid.knowledge.application.domain.weather.query.handler.BottomKQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.CompareQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.MaxQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.MinQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.TopKQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.TrendQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.ValueQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQueryType;
import io.wahid.knowledge.domain.query.Query;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryHandlerRegistry {
    private static final Logger LOGGER = Logger.getLogger(QueryHandlerRegistry.class.getName());
    private final Map<NumericQueryType, QueryHandler<NumericQuery>> numericHandlers =
            new EnumMap<>(NumericQueryType.class);

    private SemanticSearchQueryHandler semanticSearchQueryHandler;

    @SuppressWarnings("unchecked")
    public <Q extends Query> QueryHandler<Q> resolve(Q query) {
        /*QueryHandler<Q> handler = (QueryHandler<Q>) numericHandlers.entrySet().stream()
                .filter(h -> h.getValue().supports(query))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No handler found for query type: "
                                        + query.getType()
                        ));
        LOGGER.log(Level.INFO, "handler -> {0}", handler);
        return handler;*/
        if (query instanceof NumericQuery numericQuery) {
            LOGGER.log(Level.INFO, "resolving query {0}", numericQuery.getNumericType());
            QueryHandler<Q> handler = (QueryHandler<Q>) numericHandlers.get(numericQuery.getNumericType());

            if (handler == null) {
                throw new IllegalArgumentException(
                        "No handler for type " + numericQuery.getNumericType());
            }

            return handler;
        }

        if (query instanceof SemanticQuery semanticQuery) {
            LOGGER.log(Level.INFO, "resolving query {0}", semanticQuery.getType());
            return (QueryHandler<Q>) semanticSearchQueryHandler;
        }

        LOGGER.log(Level.INFO, "no handler found");
        throw new IllegalArgumentException(
                "Unsupported query: " + query.getClass());
    }

    public void register(MaxQueryHandler maxQueryHandler) {
        numericHandlers.put(NumericQueryType.MAX, maxQueryHandler);
    }

    public void register(MinQueryHandler minQueryHandler) {
        numericHandlers.put(NumericQueryType.MIN, minQueryHandler);
    }

    public void register(TopKQueryHandler topKQueryHandler) {
        numericHandlers.put(NumericQueryType.TOP_K, topKQueryHandler);
    }

    public void register(BottomKQueryHandler bottomKQueryHandler) {
        numericHandlers.put(NumericQueryType.BOTTOM_K, bottomKQueryHandler);
    }

    public void register(CompareQueryHandler compareQueryHandler) {
        numericHandlers.put(NumericQueryType.COMPARE, compareQueryHandler);
    }

    public void register(TrendQueryHandler trendQueryHandler) {
        numericHandlers.put(NumericQueryType.TREND, trendQueryHandler);
    }

    public void register(ValueQueryHandler valueQueryHandler) {
        numericHandlers.put(NumericQueryType.VALUE, valueQueryHandler);
    }

    public void register(SemanticSearchQueryHandler semanticSearchQueryHandler) {
        this.semanticSearchQueryHandler = semanticSearchQueryHandler;
    }
}