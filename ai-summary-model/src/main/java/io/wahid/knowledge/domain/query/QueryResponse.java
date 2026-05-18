package io.wahid.knowledge.domain.query;

import java.util.List;

// TODO depricate
public class QueryResponse {
    private final String answer;
    private final List<String> sources;

    public QueryResponse(String answer, List<String> sources) {
        this.answer = answer;
        this.sources = sources != null ? sources : List.of();
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getSources() {
        return sources;
    }
}
