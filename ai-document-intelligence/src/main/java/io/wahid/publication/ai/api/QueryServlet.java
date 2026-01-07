package io.wahid.publication.ai.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wahid.publication.ai.dto.QueryRequest;
import io.wahid.publication.ai.dto.QueryResponse;
import io.wahid.publication.ai.service.QueryService;
import jakarta.servlet.http.*;

import java.io.IOException;

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
            QueryRequest request =
                    objectMapper.readValue(req.getInputStream(), QueryRequest.class);

            if (request.getQuestion() == null || request.getQuestion().isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Question is required");
                return;
            }

            QueryService.QueryResult result =
                    queryService.query(request.getQuestion(), request.getTopK());

            QueryResponse response =
                    new QueryResponse(result.answer(), result.sources());

            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), response);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
