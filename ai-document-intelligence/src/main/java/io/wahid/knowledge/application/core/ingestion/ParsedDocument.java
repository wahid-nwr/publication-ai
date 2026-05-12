package io.wahid.knowledge.application.core.ingestion;

import io.wahid.knowledge.domain.document.DocumentMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a raw parsed document before domain mapping.
 *
 * This object is intentionally domain-agnostic.
 *
 * Examples:
 * - CSV rows
 * - JSON records
 * - PDF extracted text
 * - XML nodes
 * - API payloads
 */
public class ParsedDocument {

    /**
     * Original document identifier.
     */
    private final String documentId;

    /**
     * Metadata describing the uploaded document.
     */
    private final DocumentMetadata metadata;

    /**
     * Parsed structured rows/records.
     *
     * Each row is represented as a generic key-value map.
     *
     * Example CSV row:
     *
     * {
     *     "station" : "Dhaka",
     *     "date" : "2025-01-01",
     *     "temperature" : "31.5"
     * }
     */
    private final List<Map<String, Object>> records;

    /**
     * Optional extracted plain text.
     *
     * Useful for:
     * - semantic chunking
     * - embeddings
     * - RAG pipelines
     */
    private final String rawText;

    public ParsedDocument(
            String documentId,
            DocumentMetadata metadata,
            List<Map<String, Object>> records,
            String rawText
    ) {
        this.documentId = documentId;
        this.metadata = metadata;
        this.records = records != null
                ? new ArrayList<>(records)
                : new ArrayList<>();

        this.rawText = rawText;
    }

    public String getDocumentId() {
        return documentId;
    }

    public DocumentMetadata getMetadata() {
        return metadata;
    }

    public List<Map<String, Object>> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public String getRawText() {
        return rawText;
    }

    public boolean hasRecords() {
        return !records.isEmpty();
    }

    public int recordCount() {
        return records.size();
    }

    @Override
    public String toString() {
        return "ParsedDocument{" +
                "documentId='" + documentId + '\'' +
                ", metadata=" + metadata +
                ", recordCount=" + records.size() +
                '}';
    }
}