package io.wahid.knowledge.application.core.retrieval;

import io.wahid.knowledge.domain.document.DocumentType;

public class RetrievalContext {
    private int topK;
    private double similarityThreshold;
    private boolean useHybridSearch;
    private DocumentType preferredDocumentType;
}