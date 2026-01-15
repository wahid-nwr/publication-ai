package io.wahid.publication.ai.ingestion;

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
}
