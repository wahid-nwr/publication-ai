package io.wahid.knowledge.application.core.query.handler.semantic;

import io.wahid.knowledge.application.core.query.handler.QueryHandler;
import io.wahid.knowledge.application.core.query.model.SemanticQuery;
import io.wahid.knowledge.application.core.retrieval.RetrievalContext;
import io.wahid.knowledge.application.core.retrieval.RetrievalResult;
import io.wahid.knowledge.application.core.retrieval.strategy.RetrievalStrategy;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQueryType;
import io.wahid.knowledge.domain.query.QueryType;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.domain.query.result.QueryResultReference;
import io.wahid.knowledge.domain.query.result.RetrievedDocument;
import io.wahid.knowledge.infrastructure.llms.LLMClient;

import java.util.List;

public class SemanticSearchQueryHandler implements QueryHandler<SemanticQuery> {
    private final RetrievalStrategy retrievalStrategy;
    private final LLMClient llmClient;

    public SemanticSearchQueryHandler(RetrievalStrategy retrievalStrategy, LLMClient llmClient) {
        this.retrievalStrategy = retrievalStrategy;
        this.llmClient = llmClient;
    }

    @Override
    public boolean supports(SemanticQuery query) {
        return query.getType() == QueryType.SEMANTIC;
    }

    @Override
    public QueryResult handle(SemanticQuery query) throws Exception {
        RetrievalResult retrievalResult = retrievalStrategy.retrieve(query, new RetrievalContext(3));
        String context = retrievalResult.getDocuments()
                .stream()
                .map(RetrievedDocument::getContent)
                .reduce("", (a, b) -> a + "\n" + b);

        String answer = llmClient.generate("""
                Answer the question using ONLY the context below.

                If the answer is not present,
                say you don't know.

                Context:
                %s
                """.formatted(context));

        QueryResult result = new QueryResult();
        result.setQuery(query);
        result.setAnswer(answer);
        List<QueryResultReference> references =
                retrievalResult.getDocuments()
                        .stream()
                        .map(doc -> {
                            QueryResultReference ref = new QueryResultReference();
                            ref.setSourceId(doc.getDocumentId());
                            ref.setChunkId(doc.getChunkId());
                            ref.setDescription(doc.getContent());
                            return ref;
                        })
                        .toList();

        result.setReferences(references);

        return result;
    }
}