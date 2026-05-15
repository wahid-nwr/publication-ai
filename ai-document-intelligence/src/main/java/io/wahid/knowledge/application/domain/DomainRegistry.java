package io.wahid.knowledge.application.domain;

import java.util.HashMap;
import java.util.Map;

public class DomainRegistry {

    private final Map<String, DomainContext> domains = new HashMap<>();

    public void register(String domainName, DomainContext context) {
        domains.put(domainName, context);
    }

    public DomainContext get(String domainName) {
        return domains.get(domainName);
    }
}