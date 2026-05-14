package io.wahid.knowledge.domain.common;

import java.io.Serializable;
import java.util.Map;

/**
 * Base contract for all domain-specific records.
 *
 * Examples:
 * - WeatherMeasurement
 * - PublicationRecord
 * - FinancialTransaction
 * - SensorReading
 *
 * The platform layer should only depend on this abstraction.
 */
public interface DomainRecord extends Serializable {

    /**
     * Unique identifier of the record.
     */
    String getRecordId();

    /**
     * Logical domain type.
     *
     * Example:
     * WEATHER
     * PUBLICATION
     * FINANCE
     */
    DomainType getDomainType();

    /**
     * Human-readable representation used for:
     *
     * - embeddings
     * - chunking
     * - semantic retrieval
     * - LLM context
     */
    String toSearchableText();

    /**
     * Structured attributes for:
     *
     * - filtering
     * - aggregations
     * - metadata search
     * - graph relationships
     */
    Map<String, Object> getAttributes();
}