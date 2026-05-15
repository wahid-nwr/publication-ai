package io.wahid.knowledge.application.domain;

import io.wahid.knowledge.application.core.insight.InsightGenerator;
import io.wahid.knowledge.application.core.query.routing.QueryRouter;
import io.wahid.knowledge.application.core.retrieval.strategy.RetrievalStrategy;

public class DomainContext {
    private String domainName;
    private DocumentParser documentParser;
    private DomainMapper domainMapper;
    private QueryRouter queryRouter;
    private RetrievalStrategy retrievalStrategy;
    private InsightGenerator insightGenerator;
//    private DomainMetadata metadata;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public DocumentParser getDocumentParser() {
        return documentParser;
    }

    public void setDocumentParser(DocumentParser documentParser) {
        this.documentParser = documentParser;
    }

    public DomainMapper getDomainMapper() {
        return domainMapper;
    }

    public void setDomainMapper(DomainMapper domainMapper) {
        this.domainMapper = domainMapper;
    }

    public QueryRouter getQueryRouter() {
        return queryRouter;
    }

    public void setQueryRouter(QueryRouter queryRouter) {
        this.queryRouter = queryRouter;
    }

    public RetrievalStrategy getRetrievalStrategy() {
        return retrievalStrategy;
    }

    public void setRetrievalStrategy(RetrievalStrategy retrievalStrategy) {
        this.retrievalStrategy = retrievalStrategy;
    }

    public InsightGenerator getInsightGenerator() {
        return insightGenerator;
    }

    public void setInsightGenerator(InsightGenerator insightGenerator) {
        this.insightGenerator = insightGenerator;
    }
}