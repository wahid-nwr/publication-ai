package io.wahid.knowledge.domain.query.result;

import io.wahid.knowledge.domain.document.DocumentMetadata;

public class RetrievedDocument {
    private String documentId;
    private String content;
    private double score;
    private DocumentMetadata metadata;

    public RetrievedDocument(String documentId, String content, double score) {
        this.score = score;
        this.documentId = documentId;
        this.content = content;
    }
}