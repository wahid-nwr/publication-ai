package io.wahid.knowledge.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.knowledge.api.mapper.QueryResponseMapper;
import io.wahid.knowledge.api.mapper.impl.DefaultQueryResponseMapper;
import io.wahid.knowledge.domain.query.QueryRequest;
import io.wahid.knowledge.domain.query.QueryResponse;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.QueryService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class QueryServlet extends HttpServlet {

    private final QueryService queryService;

    private final QueryResponseMapper responseMapper;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public QueryServlet(
            QueryService queryService
    ) {
        this.queryService = queryService;
        this.responseMapper = new DefaultQueryResponseMapper();
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        try {

            QueryRequest request = objectMapper.readValue(req.getInputStream(), QueryRequest.class);

            if (request.getQuestion() == null || request.getQuestion().isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Question is required");
                return;
            }

            QueryResult result = queryService.query(request.getQuestion(), request.getTopK());

            QueryResponse response = responseMapper.map(result);
            resp.setContentType("application/json");

            objectMapper.writeValue(resp.getOutputStream(), response);

        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}