package io.wahid.knowledge.domain.query.result;

public class QueryResultReference {

    private String sourceType;

    private String sourceId;

    private String chunkId;

    private String description;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChunkId() {
        return this.chunkId;
    }

    public void setChunkId(String chunkId) {
        this.chunkId = chunkId;
    }
}