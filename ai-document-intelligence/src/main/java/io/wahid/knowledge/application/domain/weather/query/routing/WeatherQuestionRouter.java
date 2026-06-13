package io.wahid.knowledge.application.domain.weather.query.routing;

import io.wahid.knowledge.application.core.embedding.EmbeddingClient;
import io.wahid.knowledge.application.core.query.NumericIntentParser;
import io.wahid.knowledge.application.core.query.dto.NumericResult;
import io.wahid.knowledge.application.core.query.handler.QueryHandler;
import io.wahid.knowledge.application.core.query.handler.QueryHandlerRegistry;
import io.wahid.knowledge.application.core.query.model.SemanticQuery;
import io.wahid.knowledge.application.core.query.routing.QueryRouter;
import io.wahid.knowledge.application.core.retrieval.NumericQueryEngine;
import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;
import io.wahid.knowledge.application.domain.weather.query.model.NumericMetric;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQueryType;
import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.infrastructure.llms.LLMClient;
import io.wahid.knowledge.model.StationSummary;
import io.wahid.knowledge.repository.StationSummaryRepository;
import io.wahid.knowledge.util.JpaUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WeatherQuestionRouter implements QueryRouter {

    private final NumericIntentParser numericIntentParser;

    private final QueryHandlerRegistry registry;

    private final QueryHandler<SemanticQuery> semanticHandler;

    public WeatherQuestionRouter(NumericIntentParser numericIntentParser,
                                 QueryHandlerRegistry registry, QueryHandler<SemanticQuery> semanticHandler) {
        super();
        this.registry = registry;
        this.semanticHandler = semanticHandler;
        this.numericIntentParser = numericIntentParser;
    }

    @Override
    public QueryResult route(Query request) throws Exception {

        Optional<NumericQuery> numericQuery = numericIntentParser.parse(request.getText());

        if (numericQuery.isPresent()) {
            return registry.resolve(numericQuery.get()).handle(numericQuery.get());
        }
        SemanticQuery query = new SemanticQuery();
        query.setQuestion(request.getText());
        query.setTopK(10);
        query.setContext(request.getContext());
        query.setType(request.getType());
        return semanticHandler.handle(query);
    }
}

class WeatherQuestionRouter1 implements QueryRouter {

    private static final Logger LOGGER = Logger.getLogger(WeatherQuestionRouter.class.getName());
    private final StationSummaryRepository repository;
    private final VectorSearcher vectorSearcher;
    private final EmbeddingClient embeddingClient;
    private final LLMClient llmClient;
    private final NumericQueryEngine numericEngine;
    private final NumericIntentParser numericIntentParser;

    public WeatherQuestionRouter1(VectorSearcher qdrantSearcher,
                                  EmbeddingClient embeddingClient,
                                  LLMClient llmClient,
                                  NumericQueryEngine numericEngine,
                                  NumericIntentParser numericIntentParser) {
        super();
        this.vectorSearcher = qdrantSearcher;
        this.embeddingClient = embeddingClient;
        this.llmClient = llmClient;
        this.numericEngine = numericEngine;
        this.numericIntentParser = numericIntentParser;
        this.repository = new StationSummaryRepository(JpaUtil.getEntityManagerFactory());
    }

    /*public String answer(String question) throws Exception {

        Optional<NumericQuery> nq = numericIntentParser.parse(question);
        LOGGER.log(Level.INFO, "numeric intent present -> {0}", nq.isPresent());
        if (nq.isPresent()) {
            LOGGER.log(Level.INFO, "intent parsed numeric query -> {0}", nq.get());
            NumericResult result = numericEngine.execute(nq.get());
            if (result != null) {
                return formatNumericAnswer(nq.get(), result);
            }
        }

        return handleDescriptive(question);
    }*/

    // ------------------------- RAG handler -------------------------
    private String handleDescriptive(String tenantId, String workspaceId, String question) throws Exception {

        float[] queryVector = embeddingClient.embed(question);

        List<VectorSearcher.SearchResult> results =
                vectorSearcher.search(tenantId, workspaceId, queryVector, 3);

        String context = results.stream()
                .map(VectorSearcher.SearchResult::chunkText)
                .collect(Collectors.joining("\n"));

        return llmClient.generate("""
                Answer the question using ONLY the context below.
                If the answer is not present, say you don't know.

                Context:
                %s
                """.formatted(context));
    }

    // ------------------------- Comparison handler -------------------------

    private List<String> extractStations(String question) {
        String q = question.toLowerCase();

        return repository.findAllStations().stream()
                .filter(station -> q.contains(station.toLowerCase()))
                .toList();
    }

    // ------------------------- Numeric result formatter -------------------------
    private String formatNumericAnswer(NumericQuery query, NumericResult result) throws Exception {
        if (result == null || result.getNodes().isEmpty()) {
            return "No data available for this query.";
        }

        if (query.getNumericType() == NumericQueryType.MAX || query.getNumericType() == NumericQueryType.MIN) {
            StationSummary s = result.getNodes().getFirst();

            // Determine value and unit dynamically
            double value = getMetricValue(s, query.getMetric());
            String unit = query.getMetric().getUnit();

            return llmClient.generate(
                    "Answer in one sentence: " +
                            "The station with " +
                            (query.getNumericType() == NumericQueryType.MAX ? "the highest" : "the lowest") +
                            " " + query.getMetric() +
                            " is " + s.getStation() +
                            " with " + value + " " + unit + "."
            );
        }

        if (query.getNumericType() == NumericQueryType.TOP_K || query.getNumericType() == NumericQueryType.BOTTOM_K) {
            return getComparisonAnswers(query, result);
        }

        if (query.getNumericType() == NumericQueryType.TREND || query.getNumericType() == NumericQueryType.VALUE) {
            return result.getNodes().isEmpty() ? "" : result.getNodes().getFirst().getSummaryText();
        }
        // For TOP_K / BOTTOM_K / TREND
        return "This numeric analysis not yet supported.";
    }

    private String getComparisonAnswers(NumericQuery query, NumericResult result) {
        List<StationSummary> nodes = result.getNodes();
        StringBuilder sb = new StringBuilder();
        sb.append("Here are the ").append(query.getK()).append(" stations with ")
                .append(query.getNumericType() == NumericQueryType.TOP_K ? "highest " : "lowest ")
                .append(query.getMetric()).append(": ");
        for (int i = 0; i < nodes.size(); i++) {
            StationSummary s = nodes.get(i);
            double value = getMetricValue(s, query.getMetric());
            String unit = query.getMetric().getUnit();
            sb.append("\n").append(i + 1).append(". ").append(s.getStation())
                    .append(" – ").append(value).append(" ").append(unit);
        }
        return sb.toString();
    }

    private double getMetricValue(StationSummary ss, NumericMetric metric) {
        double value;
        switch (metric) {
            case TOTAL_RAINFALL:
                value = ss.getTotalRainfall();
                break;
            case AVG_RAINFALL:
                value = ss.getAvgRainfall();
                break;
            case AVG_SUNSHINE:
                value = ss.getAvgSunshine();
                break;
            case AVG_TEMPERATURE:
                value = ss.getAvgTemperature();
                break;
            default:
                value = 0;
        }
        return value;
    }

    @Override
    public QueryResult route(Query request) {
        return null;
    }
}
