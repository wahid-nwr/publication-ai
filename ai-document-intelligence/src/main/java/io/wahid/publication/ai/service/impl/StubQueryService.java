package io.wahid.publication.ai.service.impl;


import io.wahid.publication.ai.service.QueryService;

import java.util.List;

public class StubQueryService implements QueryService {

    @Override
    public QueryResult query(String question, int topK) {
        return new QueryResult(
                "This is a placeholder answer.",
                List.of("doc-123", "doc-456")
        );
    }

    @Override
    public String route(String question) throws Exception {
        return "";
    }
}
