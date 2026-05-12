package io.wahid.knowledge.domain.query;

public class QueryResultMetadata {

    private Integer retrievedDocuments;

    private Integer graphTraversals;

    public QueryResultMetadata() {
    }

    public Integer getRetrievedDocuments() {
        return retrievedDocuments;
    }

    public void setRetrievedDocuments(Integer retrievedDocuments) {
        this.retrievedDocuments = retrievedDocuments;
    }

    public Integer getGraphTraversals() {
        return graphTraversals;
    }

    public void setGraphTraversals(Integer graphTraversals) {
        this.graphTraversals = graphTraversals;
    }
}