package io.wahid.knowledge.application.retrieval;

import io.wahid.knowledge.application.query.dto.NumericQuery;
import io.wahid.knowledge.application.query.dto.NumericResult;

public interface NumericQueryEngine {
    NumericResult execute(NumericQuery query);
}
