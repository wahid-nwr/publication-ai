package io.wahid.knowledge.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.knowledge.domain.query.QueryRequest;
import io.wahid.knowledge.domain.query.QueryResponse;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.domain.query.result.QueryResultReference;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.QueryService;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

public class QueryServlet extends HttpServlet {

    private final QueryService queryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QueryServlet(QueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            QueryRequest request = objectMapper.readValue(req.getInputStream(), QueryRequest.class);

            if (request.getQuestion() == null || request.getQuestion().isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Question is required");
                return;
            }

            QueryResult result = queryService.query(request.getQuestion(), request.getTopK());

            List<String> sources = result.getReferences().stream().map(QueryResultReference::getDescription).toList();
            QueryResponse response = new QueryResponse(result.getAnswer(), sources);

            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), response);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
