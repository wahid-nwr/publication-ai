package io.wahid.knowledge.dto;

import io.wahid.knowledge.config.NumericMetric;
import io.wahid.knowledge.config.TrendDirection;

import java.util.List;

public record TrendResult(
        TrendDirection direction,
        double slope,
        List<YearValue> series,
        NumericMetric metric,
        String station,
        int fromYear,
        int toYear
) {}
