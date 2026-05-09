package io.wahid.knowledge.service;

import io.wahid.knowledge.dto.NumericQuery;
import io.wahid.knowledge.dto.NumericResult;

public interface NumericQueryEngine {
    NumericResult execute(NumericQuery query);
}
