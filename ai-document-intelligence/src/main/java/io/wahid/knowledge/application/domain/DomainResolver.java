package io.wahid.knowledge.application.domain;

import io.wahid.knowledge.domain.query.QueryRequest;

public class DomainResolver {

    public String resolve(QueryRequest request) {
        return "weather";
    }
}