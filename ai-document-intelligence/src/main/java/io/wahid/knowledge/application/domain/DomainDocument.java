package io.wahid.knowledge.application.domain;

import io.wahid.knowledge.domain.document.DocumentMetadata;
import io.wahid.knowledge.domain.document.DocumentType;

public interface DomainDocument {

    String documentId();

    DocumentType type();

    DocumentMetadata metadata();
}