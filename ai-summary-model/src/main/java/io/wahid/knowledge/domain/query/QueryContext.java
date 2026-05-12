package io.wahid.knowledge.domain.query;

import java.util.Map;

public class QueryContext {

    private Map<String, Object> filters;

    public QueryContext() {
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
}