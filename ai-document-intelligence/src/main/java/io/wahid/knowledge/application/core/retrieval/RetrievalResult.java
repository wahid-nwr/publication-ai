package io.wahid.knowledge.application.core.retrieval;

import io.wahid.knowledge.application.core.retrieval.strategy.RetrievalStrategy;
import io.wahid.knowledge.domain.query.result.RetrievedDocument;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the outcome of a retrieval operation.
 *
 * Retrieval may involve:
 * - vector search
 * - keyword search
 * - metadata filtering
 * - hybrid retrieval
 * - structured querying
 *
 * This object is intentionally domain-agnostic.
 */
public class RetrievalResult {

    /**
     * Retrieved matches/chunks/documents.
     */
    private List<RetrievedDocument> documents =
            new ArrayList<>();

    /**
     * Total retrieved items before pagination/limiting.
     */
    private long totalMatches;

    /**
     * Retrieval execution timestamp.
     */
    private Instant retrievedAt;

    /**
     * Retrieval strategy used.
     *
     * Examples:
     * - VECTOR
     * - HYBRID
     * - KEYWORD
     * - STRUCTURED
     */
    private RetrievalStrategy strategy;

    /**
     * Optional retrieval latency in milliseconds.
     */
    private long latencyMs;

    public RetrievalResult() {
        this.retrievedAt = Instant.now();
    }

    public RetrievalResult(List<RetrievedDocument> documents) {
        super();
        this.documents = documents;
    }

    public List<RetrievedDocument> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

    public void addDocument(
            RetrievedDocument document
    ) {
        this.documents.add(document);
    }

    public long getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(long totalMatches) {
        this.totalMatches = totalMatches;
    }

    public Instant getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(
            Instant retrievedAt
    ) {
        this.retrievedAt = retrievedAt;
    }

    public RetrievalStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(
            RetrievalStrategy strategy
    ) {
        this.strategy = strategy;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public boolean isEmpty() {
        return documents.isEmpty();
    }

    public int size() {
        return documents.size();
    }

    @Override
    public String toString() {
        return "RetrievalResult{" +
                "documents=" + documents.size() +
                ", totalMatches=" + totalMatches +
                ", strategy=" + strategy +
                ", latencyMs=" + latencyMs +
                '}';
    }
}