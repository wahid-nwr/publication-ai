package io.wahid.knowledge.application.domain;

import io.wahid.knowledge.application.core.ingestion.ParsedDocument;

import java.io.InputStream;

public interface DocumentParser {
    ParsedDocument parse(InputStream stream);
}