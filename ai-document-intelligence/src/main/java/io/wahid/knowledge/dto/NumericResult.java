package io.wahid.knowledge.dto;

import io.wahid.knowledge.model.StationSummary;
import io.wahid.knowledge.model.StationYearMetric;

import java.util.List;

public class NumericResult {

    private final List<StationSummary> nodes;
    private final List<StationYearMetric> stationYearMetrics;

    public NumericResult(List<StationSummary> nodes, List<StationYearMetric> stationYearMetrics) {
        this.nodes = nodes;
        this.stationYearMetrics = stationYearMetrics;
    }

    public List<StationSummary> getNodes() {
        return nodes;
    }

    public List<StationYearMetric> getStationYearMetrics() {
        return stationYearMetrics;
    }
}
