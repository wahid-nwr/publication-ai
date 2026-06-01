package io.wahid.knowledge.application.domain.weather.query.handler;

import io.wahid.knowledge.application.core.query.handler.QueryHandler;
import io.wahid.knowledge.application.core.retrieval.NumericQueryEngine;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQuery;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQueryType;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.infrastructure.llms.LLMClient;
import io.wahid.knowledge.model.StationSummary;
import io.wahid.knowledge.repository.StationSummaryRepository;
import io.wahid.knowledge.util.JpaUtil;

import java.util.List;
import java.util.stream.Collectors;

public class CompareQueryHandler implements QueryHandler<NumericQuery> {

    private final StationSummaryRepository repository;

    private final LLMClient llmClient;

    public CompareQueryHandler(NumericQueryEngine numericQueryEngine, LLMClient llmClient) {

        this.repository =
                new StationSummaryRepository(
                        JpaUtil.getEntityManagerFactory()
                );

        this.llmClient = llmClient;
    }

    @Override
    public boolean supports(NumericQuery query) {
        return query.getNumericType() == NumericQueryType.COMPARE;
    }

    @Override
    public QueryResult handle(NumericQuery query) throws Exception {

        List<String> stations =
                extractStations(query.getText());

        if (stations.size() < 2) {

            return buildResult(
                    "Please specify at least two stations to compare."
            );
        }

        List<StationSummary> summaries =
                repository.findByStationIn(stations);

        if (summaries.size() < 2) {

            return buildResult(
                    "Insufficient data to perform the comparison."
            );
        }

        String context = summaries.stream()
                .map(this::formatFacts)
                .collect(Collectors.joining("\n"));

        String answer = llmClient.generate("""
                Compare the following weather statistics.
                Do not infer or assume missing values.
                Use only the provided data.

                %s
                """.formatted(context));

        return buildResult(answer);
    }

    private QueryResult buildResult(String answer) {

        QueryResult result = new QueryResult();

        result.setAnswer(answer);

        return result;
    }

    private List<String> extractStations(String question) {

        String q = question.toLowerCase();

        return repository.findAllStations().stream()
                .filter(station ->
                        q.contains(station.toLowerCase()))
                .toList();
    }

    private String formatFacts(StationSummary s) {

        return """
                Station: %s
                Total Rainfall: %.1f mm
                Average Rainfall: %.2f mm
                Average Sunshine: %.2f hours
                Average Temperature: %.2f °C
                """.formatted(
                s.getStation(),
                s.getTotalRainfall(),
                s.getAvgRainfall(),
                s.getAvgSunshine(),
                s.getAvgTemperature()
        );
    }
}