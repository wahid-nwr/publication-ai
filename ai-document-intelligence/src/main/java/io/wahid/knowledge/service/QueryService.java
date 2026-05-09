package io.wahid.knowledge.service;

import java.util.List;

public interface QueryService {

    QueryResult query(String question, int topK) throws Exception;

    String route(String question) throws Exception;

    record QueryResult(String answer, List<String> sources) {}
}
