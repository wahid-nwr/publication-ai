package io.wahid.knowledge.application.core.insight;

import io.wahid.knowledge.application.core.retrieval.RetrievalResult;
import io.wahid.knowledge.domain.query.Query;

/**
 * Generates human-readable insights from retrieved
 * and/or aggregated domain data.
 *
 * This is intentionally domain-agnostic.
 *
 * Examples:
 * - weather summaries
 * - publication insights
 * - financial analysis
 * - AI-generated explanations
 * - trend observations
 */
public interface InsightGenerator<
        Q extends Query,
        R extends InsightResult> {

    /**
     * Generates insights from a query and retrieval result.
     */
    R generate(
            Q query,
            RetrievalResult retrievalResult
    );
}