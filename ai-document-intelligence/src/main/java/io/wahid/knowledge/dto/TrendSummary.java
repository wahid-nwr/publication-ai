package io.wahid.knowledge.dto;

import io.wahid.knowledge.config.NumericMetric;
import io.wahid.knowledge.config.TrendDirection;

public record TrendSummary(
        NumericMetric metric,
        int startYear,
        double startValue,
        int endYear,
        double endValue,
        double minValue,
        int minYear,
        double maxValue,
        int maxYear,
        double slope,
        TrendDirection direction
) {}
