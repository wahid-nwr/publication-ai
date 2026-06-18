package io.wahid.knowledge.application.core.query.model;

import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.QueryType;

import java.util.List;
import java.util.Map;

public class SemanticQuery extends Query {

    /**
     * Natural language question.
     */
    private final String question;

    /**
     * Optional semantic filters.
     * Example:
     * - documentType=WEATHER
     * - station=Dhaka
     * - year=2025
     */
    private Map<String, Object> filters;

    /**
     * Number of documents/chunks to retrieve.
     */
    private int topK = 3;

    /**
     * Minimum similarity threshold.
     */
    private double similarityThreshold = 0.7;

    /**
     * Optional target document types.
     */
    private List<String> targetDocumentTypes;

    /**
     * Whether hybrid retrieval should be used.
     */
    private boolean hybridSearchEnabled = true;

    public SemanticQuery(Query query) {
        super(query.getTenantId(), query.getWorkspaceId(), query.getText(),
                query.getIntent(), QueryType.SEMANTIC, query.getContext());
        this.question = query.getText();
        this.topK = 10;
    }

    public String getQuestion() {
        return question;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public int getTopK() {
        return topK;
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public List<String> getTargetDocumentTypes() {
        return targetDocumentTypes;
    }

    public boolean isHybridSearchEnabled() {
        return hybridSearchEnabled;
    }

    @Override
    public String getText() {
        return question;
    }

    @Override
    public String toString() {
        return "SemanticQuery{" +
                "question='" + question + '\'' +
                ", topK=" + topK +
                ", similarityThreshold=" + similarityThreshold +
                ", hybridSearchEnabled=" + hybridSearchEnabled +
                '}';
    }
}