package io.wahid.knowledge.domain.query;

import java.util.List;

// TODO depricate
public class QueryResponse {
    private String answer;
    private List<String> sources;

    public QueryResponse(String answer, List<String> sources) {
        this.answer = answer;
        this.sources = sources;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getSources() {
        return sources;
    }
}
