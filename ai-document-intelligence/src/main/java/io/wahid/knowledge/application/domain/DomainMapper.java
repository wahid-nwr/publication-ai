package io.wahid.knowledge.application.domain;

import io.wahid.knowledge.application.core.ingestion.ParsedDocument;

import java.util.List;

public interface DomainMapper<T> {
    List<T> map(ParsedDocument document);
}