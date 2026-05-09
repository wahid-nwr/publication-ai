package io.wahid.knowledge.domain;

public interface SummaryFormatter<T> {
    String format(SummaryStats<T> stats);
}
