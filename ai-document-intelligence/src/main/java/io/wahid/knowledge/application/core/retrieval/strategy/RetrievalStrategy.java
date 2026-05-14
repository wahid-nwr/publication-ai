package io.wahid.knowledge.application.core.retrieval.strategy;

import io.wahid.knowledge.application.core.retrieval.RetrievalContext;
import io.wahid.knowledge.application.core.retrieval.RetrievalResult;
import io.wahid.knowledge.domain.query.Query;

public interface RetrievalStrategy {
    RetrievalResult retrieve(Query query, RetrievalContext context) throws Exception;
}