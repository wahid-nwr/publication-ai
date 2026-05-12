package io.wahid.knowledge.domain.query;

import io.wahid.knowledge.domain.insight.Insight;

import java.util.List;

public class QueryResult {

    private Query query;

    private QueryResultType type;

    private String answer;

    private List<QueryResultReference> references;

    private List<Insight> insights;

    private QueryResultMetadata metadata;

    public QueryResult() {
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
}