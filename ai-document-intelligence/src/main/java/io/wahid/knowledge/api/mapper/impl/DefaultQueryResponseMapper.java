package io.wahid.knowledge.api.mapper.impl;

import io.wahid.knowledge.api.mapper.QueryResponseMapper;
import io.wahid.knowledge.application.core.query.model.SemanticResultPayload;
import io.wahid.knowledge.application.core.query.result.NumericExecutionResult;
import io.wahid.knowledge.application.core.query.result.NumericResultPayload;
import io.wahid.knowledge.domain.query.QueryResponse;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.domain.query.result.QueryResultReference;
import io.wahid.knowledge.domain.query.result.RetrievedDocument;
import io.wahid.knowledge.model.StationSummary;

import java.util.List;

public class DefaultQueryResponseMapper implements QueryResponseMapper {

    @Override
    public QueryResponse map(QueryResult result) {

        Object payload = result.getPayload();

        System.out.println(payload);

        /*
         * Semantic response
         */
        if (payload instanceof SemanticResultPayload semanticPayload) {
            List<String> sources = semanticPayload.results()
                            .stream()
                            .map(RetrievedDocument::getDocumentId)
                            .distinct()
                            .toList();

            String answer = semanticPayload.results()
                            .stream()
                            .map(RetrievedDocument::getContent)
                            .limit(3)
                            .reduce("", (a, b) -> a + "\n" + b);

            return new QueryResponse(
                    answer,
                    sources
            );
        }

        /*
         * Numeric response
         */
        if (payload instanceof NumericResultPayload numeric) {

            List<StationSummary> stations =
                    numeric.getStations();

            if (stations == null || stations.isEmpty()) {
                return new QueryResponse(
                        "No numeric result found.",
                        List.of()
                );
            }

            String answer = stations.stream()
                    .limit(5)
                    .map(s -> formatStation(s, numeric))
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("No result.");

            return new QueryResponse(
                    answer,
                    List.of()
            );
        }

        /*
         * Generic references
         */
        List<String> sources =
                result.getReferences() != null
                        ? result.getReferences()
                        .stream()
                        .map(QueryResultReference::getDescription)
                        .toList()
                        : List.of();

        return new QueryResponse(
                "No response available",
                sources
        );
    }

    private String formatStation(
            StationSummary s,
            NumericResultPayload payload
    ) {

        return switch (payload.getMetric()) {

            case TOTAL_RAINFALL ->
                    s.getStation() + " -> " +
                            s.getTotalRainfall() + " mm";

            case AVG_RAINFALL ->
                    s.getStation() + " -> " +
                            s.getAvgRainfall() + " mm";

            case AVG_TEMPERATURE ->
                    s.getStation() + " -> " +
                            s.getAvgTemperature() + " °C";

            case AVG_SUNSHINE ->
                    s.getStation() + " -> " +
                            s.getAvgSunshine() + " hours";

            case AVG_HUMIDITY ->
                    s.getStation() + " -> " +
                            s.getAvgHumidity() + " %";

            case MIN_TEMPERATURE ->
                    s.getStation() + " -> " +
                            s.getMinTemperature() + " °C";

            case MAX_TEMPERATURE ->
                    s.getStation() + " -> " +
                            s.getMaxTemperature() + " °C";

            default ->
                    s.getStation();
        };
    }
}
