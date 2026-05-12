package io.wahid.knowledge.application.core.insight;

import io.wahid.knowledge.domain.document.DocumentMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base result for generated insights.
 *
 * This object represents the final interpreted
 * or generated understanding produced by the system.
 *
 * Examples:
 * - weather summaries
 * - trend explanations
 * - anomaly observations
 * - AI-generated narratives
 * - recommendation outputs
 */
public abstract class InsightResult {

    /**
     * Human-readable insight summary.
     */
    private String summary;

    /**
     * Confidence score for the generated insight.
     *
     * Optional:
     * - rule engine confidence
     * - semantic ranking confidence
     * - LLM confidence
     */
    private double confidenceScore;

    /**
     * Timestamp when insight was generated.
     */
    private Instant generatedAt;

    /**
     * Metadata associated with the insight.
     */
    private DocumentMetadata metadata;

    /**
     * Additional extensible attributes.
     *
     * Useful for:
     * - token usage
     * - latency
     * - model name
     * - domain-specific fields
     * - debug information
     */
    private final Map<String, Object> attributes =
            new HashMap<>();

    protected InsightResult() {
        this.generatedAt = Instant.now();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(
            double confidenceScore
    ) {
        this.confidenceScore = confidenceScore;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(
            Instant generatedAt
    ) {
        this.generatedAt = generatedAt;
    }

    public DocumentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(
            DocumentMetadata metadata
    ) {
        this.metadata = metadata;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void addAttribute(
            String key,
            Object value
    ) {
        attributes.put(key, value);
    }

    @Override
    public String toString() {
        return "InsightResult{" +
                "summary='" + summary + '\'' +
                ", confidenceScore=" + confidenceScore +
                ", generatedAt=" + generatedAt +
                '}';
    }
}