package io.wahid.knowledge.domain.query.result;

import io.wahid.knowledge.domain.document.DocumentMetadata;

public class RetrievedDocument {
    private String documentId;
    private String chunkId;
    private String content;
    private double score;
    private DocumentMetadata metadata;

    public RetrievedDocument(String documentId, String chunkId, String content, double score) {
        this.chunkId = chunkId;
        this.score = score;
        this.documentId = documentId;
        this.content = content;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public DocumentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(DocumentMetadata metadata) {
        this.metadata = metadata;
    }

    public String getChunkId() {
        return this.chunkId;
    }
}