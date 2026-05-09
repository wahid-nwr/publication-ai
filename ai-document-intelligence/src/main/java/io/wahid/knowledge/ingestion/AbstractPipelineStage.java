package io.wahid.knowledge.ingestion;

import io.wahid.knowledge.dto.TextChunk;

public abstract class AbstractPipelineStage implements PipelineStage {

    protected PipelineStage downstream;

    public void setDownstream(PipelineStage downstream) {
        this.downstream = downstream;
    }

    public PipelineStage getDownstream() {
        return this.downstream;
    }

    @Override
    public void flush() throws Exception {
        if (downstream != null) {
            downstream.flush();
        }
    }

    protected void forward(TextChunk chunk) throws Exception {
        if (downstream != null) {
            downstream.accept(chunk);
        }
    }
}
