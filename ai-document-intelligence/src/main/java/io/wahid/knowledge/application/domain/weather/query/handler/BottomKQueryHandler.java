package io.wahid.knowledge.application.domain.weather.query.handler;

import io.wahid.knowledge.application.core.query.handler.QueryHandler;
import io.wahid.knowledge.application.core.query.result.NumericExecutionResult;
import io.wahid.knowledge.application.core.query.result.NumericResultPayload;
import io.wahid.knowledge.application.core.retrieval.NumericQueryEngine;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.infrastructure.llms.LLMClient;

import java.util.List;

public class BottomKQueryHandler implements QueryHandler<NumericQuery> {
    private final NumericQueryEngine weatherQueryEngine;
    private final LLMClient llmClient;

    public BottomKQueryHandler(
            NumericQueryEngine weatherQueryEngine, LLMClient llmClient
    ) {
        this.weatherQueryEngine = weatherQueryEngine;
        this.llmClient = llmClient;
    }

    @Override
    public boolean supports(Query query) {
        return query instanceof NumericQuery /*nq && nq.getType() == NumericQueryType.MAX*/;
    }

    @Override
    public QueryResult handle(NumericQuery query) {

//        String metric = query.getMetric().getMetricName();

//        NumericResultPayload result = weatherQueryEngine.execute(query);//repository.findTopByMetricDesc(metric);

        return new QueryResult(
                query,
                weatherQueryEngine.execute(query)
        );
    }
}
