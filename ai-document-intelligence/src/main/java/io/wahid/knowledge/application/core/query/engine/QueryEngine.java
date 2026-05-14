package io.wahid.knowledge.application.core.query.engine;

import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.result.QueryResult;

/**
 * Core contract for executing queries.
 *
 * The core layer should NOT know anything about:
 * - weather
 * - finance
 * - publication
 * - analytics specifics
 *
 * Domain modules implement this interface.
 *
 * Examples:
 *
 * WeatherQueryEngine
 * PublicationQueryEngine
 * FinancialQueryEngine
 */
public interface QueryEngine<Q extends Query, R extends QueryResult> {

    /**
     * Executes a query and returns a result.
     */
    R execute(Q query) throws Exception;
}