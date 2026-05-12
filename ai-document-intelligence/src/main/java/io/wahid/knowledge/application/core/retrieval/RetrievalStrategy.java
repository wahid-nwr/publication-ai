package io.wahid.knowledge.application.core.retrieval;

import io.wahid.knowledge.domain.query.Query;

public interface RetrievalStrategy {
    RetrievalResult retrieve(Query query, RetrievalContext context);
}