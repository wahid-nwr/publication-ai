package io.wahid.knowledge.infrastructure.vectorstore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentPayload {
    @JsonProperty("documentId")
    private final String documentId;
    @JsonProperty("text")
    private final String text;
    @JsonProperty("payload")
    private final String source;
    @JsonProperty("score")
    private final double score;

    protected DocumentPayload() {
        this.documentId = null;
        this.score = 0;
        this.text = null;
        this.source = null;
    }

    public DocumentPayload(String documentId, String text, String source, double score) {
        this.documentId = documentId;
        this.text = text;
        this.source = source;
        this.score = score;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getText() {
        return text;
    }

    public String getSource() {
        return source;
    }

    public double getScore() {
        return score;
    }
}
