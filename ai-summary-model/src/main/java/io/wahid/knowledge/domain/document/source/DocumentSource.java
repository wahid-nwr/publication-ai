package io.wahid.knowledge.domain.document.source;

import java.io.InputStream;
import java.util.Map;

/**
 * Generic abstraction representing an incoming document source.
 *
 * This decouples:
 *
 * - servlet upload
 * - local file
 * - S3 object
 * - URL
 * - database blob
 * - Kafka message
 *
 * from the ingestion pipeline.
 */
public interface DocumentSource {

    /**
     * Unique source identifier.
     */
    String getSourceId();

    /**
     * Original file/document name.
     */
    String getName();

    /**
     * Mime/content type.
     *
     * Example:
     * text/csv
     * application/json
     * text/plain
     */
    String getContentType();

    /**
     * Raw content stream.
     */
    InputStream getInputStream();

    /**
     * Additional source metadata.
     */
    Map<String, Object> getMetadata();

    /**
     * Source type.
     */
    DocumentSourceType getSourceType();

    /**
     * Estimated content length if known.
     */
    long getContentLength();
}