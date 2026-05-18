package io.wahid.knowledge.api.mapper;

import io.wahid.knowledge.domain.query.QueryResponse;
import io.wahid.knowledge.domain.query.result.QueryResult;

public interface QueryResponseMapper {

    QueryResponse map(QueryResult result);
}