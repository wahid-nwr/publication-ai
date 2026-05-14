package io.wahid.knowledge.infrastructure.document.source;

import io.wahid.knowledge.domain.document.source.DocumentSource;
import io.wahid.knowledge.domain.document.source.DocumentSourceType;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MultipartDocumentSource implements DocumentSource {

    private final String sourceId;

    private final Part part;

    public MultipartDocumentSource(String sourceId,
                                   Part part) {

        this.sourceId = sourceId;
        this.part = part;
    }

    @Override
    public String getSourceId() {
        return sourceId;
    }

    @Override
    public String getName() {
        return part.getSubmittedFileName();
    }

    @Override
    public String getContentType() {
        return part.getContentType();
    }

    @Override
    public InputStream getInputStream() {

        try {
            return part.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> getMetadata() {

        Map<String, Object> metadata = new HashMap<>();

        metadata.put("size", part.getSize());
        metadata.put("submittedFileName", part.getSubmittedFileName());

        return metadata;
    }

    @Override
    public DocumentSourceType getSourceType() {
        return DocumentSourceType.FILE_UPLOAD;
    }

    @Override
    public long getContentLength() {
        return part.getSize();
    }
}