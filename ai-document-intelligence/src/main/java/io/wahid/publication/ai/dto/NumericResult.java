package io.wahid.publication.ai.dto;

import io.wahid.publication.ai.model.StationSummary;

import java.util.List;

public class NumericResult {

    private final List<StationSummary> nodes;

    public NumericResult(List<StationSummary> nodes) {
        this.nodes = nodes;
    }

    public List<StationSummary> getNodes() {
        return nodes;
    }
}
