package io.wahid.knowledge.application.core.aggregation;

import io.wahid.knowledge.domain.query.Query;

/**
 * Generic aggregation contract.
 *
 * Aggregators are responsible for:
 * - grouping
 * - summarizing
 * - statistical computation
 * - trend analysis
 * - domain-specific rollups
 *
 * The core layer remains domain-agnostic.
 */
public interface Aggregator<T, R> {

    /**
     * Aggregates domain records into a result.
     */
    R aggregate(Iterable<T> records);

    /**
     * Optional query-aware aggregation.
     *
     * Useful for:
     * - filtering
     * - dynamic metrics
     * - grouped aggregations
     * - time-window analysis
     */
    default R aggregate(
            Iterable<T> records,
            Query query
    ) {
        return aggregate(records);
    }
}