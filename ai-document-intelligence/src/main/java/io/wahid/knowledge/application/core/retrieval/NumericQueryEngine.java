package io.wahid.knowledge.application.core.retrieval;

import io.wahid.knowledge.application.core.query.result.NumericResultPayload;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;

public interface NumericQueryEngine {
    NumericResultPayload execute(NumericQuery query);
}
