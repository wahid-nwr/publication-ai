package io.wahid.knowledge.application.core.query.dto;

import io.wahid.knowledge.application.core.query.NumericMetric;
import io.wahid.knowledge.domain.query.TrendDirection;
import io.wahid.knowledge.application.core.ingestion.processing.dto.YearValue;

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
