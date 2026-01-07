package io.wahid.publication.ai.service;

import java.util.List;

public interface QueryService {

    QueryResult query(String question, int topK) throws Exception;

    record QueryResult(String answer, List<String> sources) {}
}
