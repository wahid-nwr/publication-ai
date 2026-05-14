package io.wahid.knowledge.domain.query.result;

import java.util.List;

public interface QueryResultPayload<E> {
    List<E> results();
}