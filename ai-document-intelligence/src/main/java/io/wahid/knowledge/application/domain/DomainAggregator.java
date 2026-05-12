package io.wahid.knowledge.application.domain;

import java.util.List;

public interface DomainAggregator<T, R> {

    R aggregate(List<T> records);
}