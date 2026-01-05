package io.wahid.publication.ai.dto;

import java.util.List;

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
