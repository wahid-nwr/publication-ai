package io.wahid.knowledge.application.domain.weather.query.handler;

import io.wahid.knowledge.application.core.query.dto.GraphResult;
import io.wahid.knowledge.application.core.query.handler.QueryHandler;
import io.wahid.knowledge.application.core.query.result.NumericResultPayload;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.impl.Neo4jWeatherRepository;

import java.util.List;

public class MaxQueryHandler
        implements QueryHandler<NumericQuery> {

    private final Neo4jWeatherRepository repository;

    public MaxQueryHandler(
            Neo4jWeatherRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public boolean supports(Query query) {
        return query instanceof NumericQuery nq
                /*&& nq.getType() == NumericQueryType.MAX*/;
    }

    @Override
    public QueryResult handle(NumericQuery query) {

        String metric = query.getMetric().getMetricName();

        GraphResult result = repository.findTopByMetricDesc(metric);

        return new QueryResult(
                query,
                new NumericResultPayload(List.of(result))
        );
    }
}