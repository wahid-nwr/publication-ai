package io.wahid.publication.ai.dto;

import io.wahid.publication.ai.config.NumericMetric;
import io.wahid.publication.ai.config.TrendDirection;

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
