package io.wahid.publication.ai.dto;

public record GraphResult(
        String station,
        String metric,
        double value,
        int startYear,
        int endYear,
        int rank
) {}
