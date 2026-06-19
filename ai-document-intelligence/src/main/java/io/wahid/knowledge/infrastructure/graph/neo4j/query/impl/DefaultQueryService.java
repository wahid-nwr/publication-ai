package io.wahid.knowledge.infrastructure.graph.neo4j.query.impl;

import io.wahid.knowledge.application.core.retrieval.Retriever;
import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;
import io.wahid.knowledge.application.domain.DomainContext;
import io.wahid.knowledge.application.domain.DomainRegistry;
import io.wahid.knowledge.application.domain.DomainResolver;
import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.domain.query.result.QueryResultReference;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.QueryService;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultQueryService implements QueryService {
    private static final Logger LOGGER = Logger.getLogger(DefaultQueryService.class.getName());
    private final Retriever retriever;
    private final DomainResolver domainResolver;
    private final DomainRegistry domainRegistry;

    public DefaultQueryService(
            DomainRegistry domainRegistry,
            DomainResolver domainResolver,
            Retriever retriever
    ) {
        this.retriever = retriever;
        this.domainRegistry = domainRegistry;
        this.domainResolver = domainResolver;
    }

    @Override
    public QueryResult query(String tenantId, String workspaceId, String question, int topK) throws Exception {

        /*
         * Generic retrieval
         */
        LOGGER.info("---- Retrieving Context ----");
        LOGGER.log(Level.INFO, "tenant-> {0}, workspace-> {1}", new Object[]{tenantId, workspaceId});
        List<VectorSearcher.SearchResult> retrieved = retriever.retrieve(tenantId, workspaceId, question, topK);

        LOGGER.info("---- Retrieved Context ----");
        LOGGER.log(Level.INFO, "Chunk texts. First 10: {}",
                retrieved.stream()
                        .map(VectorSearcher.SearchResult::chunkText)
                        .limit(10)
                        .toList().toArray());

        LOGGER.info("---------------------------");

        /*
         * Resolve domain
         */
        Query query = new Query(tenantId, workspaceId, question, null, null, null);
        String domain = domainResolver.resolve(query);

        /*
         * Get domain context
         */
        DomainContext context = domainRegistry.get(domain);

        if (context == null) {
            throw new IllegalStateException(
                    "No domain registered for: " + domain
            );
        }

        /*
         * Build sources
         */
        List<QueryResultReference> sources = retrieved.stream()
                .map(result -> {
                    QueryResultReference reference = new QueryResultReference();
                    reference.setSourceId(result.documentId());
                    reference.setChunkId(result.chunkId());
                    reference.setSourceType(result.metadata().getSource());
                    reference.setDescription(result.metadata().getText());
                    return reference;
                })
                .distinct()
                .toList();

        /*
         * Route question to domain
         */
        QueryResult queryResult = context.getQueryRouter().route(query);
        queryResult.setReferences(sources);
        return context.getQueryRouter().route(query);
    }
}

