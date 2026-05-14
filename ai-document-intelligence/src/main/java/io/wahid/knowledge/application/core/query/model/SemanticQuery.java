package io.wahid.knowledge.application.core.query.model;

import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.QueryType;

import java.util.List;
import java.util.Map;

public class SemanticQuery extends Query {

    /**
     * Natural language question.
     */
    private String question;

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

    public SemanticQuery() {
        setType(QueryType.SEMANTIC);
    }

    public SemanticQuery(String question) {
        this.question = question;
        setType(QueryType.SEMANTIC);
    }

    public SemanticQuery(String question, int topK) {
        this.question = question;
        this.topK = topK;
        setType(QueryType.SEMANTIC);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public List<String> getTargetDocumentTypes() {
        return targetDocumentTypes;
    }

    public void setTargetDocumentTypes(List<String> targetDocumentTypes) {
        this.targetDocumentTypes = targetDocumentTypes;
    }

    public boolean isHybridSearchEnabled() {
        return hybridSearchEnabled;
    }

    public void setHybridSearchEnabled(boolean hybridSearchEnabled) {
        this.hybridSearchEnabled = hybridSearchEnabled;
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