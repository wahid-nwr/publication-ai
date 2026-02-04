package io.wahid.publication.ai.service;

import io.wahid.publication.ai.dto.NumericQuery;
import io.wahid.publication.ai.dto.NumericResult;

public interface NumericQueryEngine {
    NumericResult execute(NumericQuery query);
}
