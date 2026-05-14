package io.wahid.knowledge.application.core.query.model;

import io.wahid.knowledge.domain.query.result.QueryResultPayload;
import io.wahid.knowledge.domain.query.result.RetrievedDocument;

import java.util.List;

public class SemanticResultPayload implements QueryResultPayload {
    private List<RetrievedDocument> results;
    public SemanticResultPayload(List<RetrievedDocument> documents) {
        this.results = documents;
    }

    @Override
    public List<RetrievedDocument> results() {
        return this.results;
    }
}
