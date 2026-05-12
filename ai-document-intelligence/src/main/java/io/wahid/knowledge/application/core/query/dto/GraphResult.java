package io.wahid.knowledge.application.core.query.dto;

public record GraphResult(
        String station,
        String metric,
        double value,
        int startYear,
        int endYear,
        int rank
) {}
