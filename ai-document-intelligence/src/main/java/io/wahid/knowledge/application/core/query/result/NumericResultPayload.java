package io.wahid.knowledge.application.core.query.result;

import io.wahid.knowledge.domain.query.result.QueryResultPayload;
import io.wahid.knowledge.domain.query.result.RetrievedDocument;

import java.util.List;

public class NumericResultPayload implements QueryResultPayload {
    private Double value;
    private List<?> result;

    public <E> NumericResultPayload(List<E> result) {
        this.result = result;
    }

    @Override
    public List<?> results() {
        return this.result;
    }
}