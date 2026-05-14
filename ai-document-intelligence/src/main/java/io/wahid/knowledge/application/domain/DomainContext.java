package io.wahid.knowledge.application.domain;

import io.wahid.knowledge.application.core.aggregation.Aggregator;
import io.wahid.knowledge.application.core.insight.InsightGenerator;
import io.wahid.knowledge.application.core.query.engine.QueryEngine;
import io.wahid.knowledge.application.core.retrieval.Retriever;

public class DomainContext {
    private String domain;
    private QueryEngine queryEngine;
    private Retriever retriever;
    private Aggregator aggregator;
    private InsightGenerator insightGenerator;
}