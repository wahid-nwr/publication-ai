package io.wahid.knowledge.application.core.retrieval;

import io.wahid.knowledge.domain.document.DocumentMetadata;

public class RetrievedDocument {
    private String documentId;
    private String content;
    private double score;
    private DocumentMetadata metadata;
}