package io.wahid.knowledge.application.domain.weather.query.handler;

import io.wahid.knowledge.application.core.query.handler.QueryHandler;
import io.wahid.knowledge.application.core.retrieval.NumericQueryEngine;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQueryType;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.infrastructure.llms.LLMClient;

public class ValueQueryHandler implements QueryHandler<NumericQuery> {
    private final NumericQueryEngine weatherQueryEngine;
    private final LLMClient llmClient;

    public ValueQueryHandler(
            NumericQueryEngine weatherQueryEngine, LLMClient llmClient
    ) {
        this.weatherQueryEngine = weatherQueryEngine;
        this.llmClient = llmClient;
    }

    @Override
    public boolean supports(NumericQuery query) {
        return query.getNumericType() == NumericQueryType.VALUE;
    }

    @Override
    public QueryResult handle(NumericQuery query) {

//        String metric = query.getMetric().getMetricName();

        return new QueryResult(
                query,
                weatherQueryEngine.execute(query)
        );
    }
}
