package io.wahid.knowledge.application.domain;

import io.wahid.knowledge.domain.document.DocumentType;

import java.io.InputStream;

public interface DocumentParser<T extends DomainDocument> {

    boolean supports(DocumentType type);

    T parse(InputStream inputStream);
}