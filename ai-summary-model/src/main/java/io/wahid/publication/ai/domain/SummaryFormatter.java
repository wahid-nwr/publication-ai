package io.wahid.publication.ai.domain;

public interface SummaryFormatter<T> {
    String format(SummaryStats<T> stats);
}
