package io.wahid.knowledge.application.core.retrieval;

import io.wahid.knowledge.application.core.query.handler.numeric.NumericQuery;
import io.wahid.knowledge.application.core.query.dto.NumericResult;

public interface NumericQueryEngine {
    NumericResult execute(NumericQuery query);
}
