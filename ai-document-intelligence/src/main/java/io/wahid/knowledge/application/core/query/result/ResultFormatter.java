package io.wahid.knowledge.application.core.query.result;

import io.wahid.knowledge.domain.query.result.QueryResult;

public interface ResultFormatter {

    boolean supports(QueryResult result);

    String format(QueryResult result) throws Exception;
}