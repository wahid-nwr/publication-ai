package io.wahid.publication.ai.dto;

import io.wahid.publication.ai.config.TrendDirection;

import java.util.List;

public record TrendResult(
        TrendDirection direction,
        double slope,
        List<YearValue> series
) {}
