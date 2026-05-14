package io.wahid.knowledge.domain.query.result;

import io.wahid.knowledge.domain.insight.Insight;
import io.wahid.knowledge.domain.query.Query;

import java.util.List;
import java.util.Map;

public class QueryResult {

    private Query query;

    private QueryResultType type;

    /**
     * Final generated answer.
     */
    private String answer;

    /**
     * Structured/raw execution payload.
     */
    private Object payload;

    /**
     * Retrieved supporting documents.
     */
    private List<RetrievedDocument> retrievedDocuments;

    /**
     * References/citations.
     */
    private List<QueryResultReference> references;

    /**
     * Generated insights.
     */
    private List<Insight> insights;

    /**
     * Additional execution metadata.
     */
    private QueryResultMetadata metadata;

    /**
     * Optional execution attributes.
     */
    private Map<String, Object> attributes;


    public QueryResult() {
    }

    public QueryResult(Query query, Object payload) {
        this.query = query;
        this.payload = payload;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public QueryResultType getType() {
        return type;
    }

    public void setType(QueryResultType type) {
        this.type = type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<QueryResultReference> getReferences() {
        return references;
    }

    public void setReferences(List<QueryResultReference> references) {
        this.references = references;
    }

    public List<Insight> getInsights() {
        return insights;
    }

    public void setInsights(List<Insight> insights) {
        this.insights = insights;
    }

    public QueryResultMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(QueryResultMetadata metadata) {
        this.metadata = metadata;
    }

    public Object getPayload() {
        return this.payload;
    }
}