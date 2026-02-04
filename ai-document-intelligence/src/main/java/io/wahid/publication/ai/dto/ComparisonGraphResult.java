package io.wahid.publication.ai.dto;

import java.util.Map;

public record ComparisonGraphResult(
        String metric,
        Map<String, Double> stationValues
) {}
