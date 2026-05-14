package io.wahid.knowledge.application.core.query.handler.numeric;

import io.wahid.knowledge.application.core.query.handler.QueryHandler;
import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.QueryType;
import io.wahid.knowledge.domain.query.result.QueryResult;

public class TrendQueryHandler implements QueryHandler<NumericQuery> {

    @Override
    public boolean supports(Query query) {
        return query.getType() == QueryType.HYBRID;
    }

    @Override
    public QueryResult handle(NumericQuery query) {
        return handleTrend(query);
    }

    private QueryResult handleTrend(NumericQuery query) {
        return new QueryResult();
    }
}