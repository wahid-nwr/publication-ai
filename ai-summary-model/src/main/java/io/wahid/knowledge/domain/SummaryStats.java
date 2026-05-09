package io.wahid.knowledge.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SummaryStats<T> {

    public String dataset;
    public String location;
    public LocalDate startDate;
    public LocalDate endDate;
    public long rowCount;

    public Map<String, MetricStats> metrics;
    public List<String> patterns;
    public List<String> outliers;
}
