package io.wahid.knowledge.application.domain;

import io.wahid.knowledge.domain.query.Query;

public class DomainResolver {

    public String resolve(Query request) {
        return "weather";
    }
}