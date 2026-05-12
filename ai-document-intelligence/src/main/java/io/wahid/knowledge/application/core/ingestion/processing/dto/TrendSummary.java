package io.wahid.knowledge.application.core.ingestion.processing.dto;

import io.wahid.knowledge.application.core.query.NumericMetric;
import io.wahid.knowledge.domain.query.TrendDirection;

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
