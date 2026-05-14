package io.wahid.knowledge.application.core.query.handler.semantic;

import io.wahid.knowledge.application.core.query.handler.QueryHandler;
import io.wahid.knowledge.application.core.query.model.SemanticQuery;
import io.wahid.knowledge.application.core.query.model.SemanticResultPayload;
import io.wahid.knowledge.application.core.retrieval.RetrievalContext;
import io.wahid.knowledge.application.core.retrieval.RetrievalResult;
import io.wahid.knowledge.application.core.retrieval.strategy.RetrievalStrategy;
import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.result.QueryResult;

public class SemanticSearchQueryHandler
        implements QueryHandler<SemanticQuery> {

    private final RetrievalStrategy retrievalStrategy;

    public SemanticSearchQueryHandler(
            RetrievalStrategy retrievalStrategy
    ) {
        this.retrievalStrategy = retrievalStrategy;
    }

    @Override
    public boolean supports(Query query) {
        return query instanceof SemanticQuery;
    }

    @Override
    public QueryResult handle(SemanticQuery query) throws Exception {

        RetrievalResult retrievalResult =
                retrievalStrategy.retrieve(
                        query,
                        new RetrievalContext(3)
                );

        return new QueryResult(
                query,
                new SemanticResultPayload(
                        retrievalResult.getDocuments()
                )
        );
    }
}
