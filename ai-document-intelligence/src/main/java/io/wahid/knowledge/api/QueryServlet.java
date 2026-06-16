package io.wahid.knowledge.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
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
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(QueryServlet.class.getName());
    private final transient QueryService queryService;
    private final transient QueryResponseMapper responseMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QueryServlet(QueryService queryService) {
        this.queryService = queryService;
        this.responseMapper = new DefaultQueryResponseMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            JWTClaimsSet claims = (JWTClaimsSet) req.getAttribute("jwtClaims");
            String tenantId;
            try {
                List<String> tenantIds = claims.getStringListClaim("tenantId");
                tenantId = tenantIds.getFirst();
            } catch (ParseException e) {
                LOGGER.log(Level.SEVERE, "ParseException occured.", e);
                throw new RuntimeException(e);
            }
            String workspaceId = Optional.ofNullable(req.getAttribute("workspaceId").toString()).orElse("");
            QueryRequest request = objectMapper.readValue(req.getInputStream(), QueryRequest.class);
            if (request.getQuestion() == null || request.getQuestion().isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Question is required");
                return;
            }
            request.setTenantId(tenantId);
            request.setWorkspaceId(workspaceId);
            System.out.println("---- Query servlet ----");
            System.out.println("tenant->" + tenantId + ", workspace->" + workspaceId);
            QueryResult result = queryService.query(request.getTenantId(), request.getWorkspaceId(),
                    request.getQuestion(), request.getTopK());
            QueryResponse response = responseMapper.map(result);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Query exception occurred,", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }
}