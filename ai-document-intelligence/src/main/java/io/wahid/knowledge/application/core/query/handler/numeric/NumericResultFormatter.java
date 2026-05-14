package io.wahid.knowledge.application.core.query.handler.numeric;

import io.wahid.knowledge.application.core.query.result.NumericResultPayload;
import io.wahid.knowledge.application.core.query.result.ResultFormatter;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.infrastructure.llms.LLMClient;
import io.wahid.knowledge.model.StationSummary;

public class NumericResultFormatter
        implements ResultFormatter {

    private final LLMClient llmClient;

    public NumericResultFormatter(
            LLMClient llmClient
    ) {
        this.llmClient = llmClient;
    }

    @Override
    public boolean supports(QueryResult result) {

        return result.getPayload()
                instanceof NumericResultPayload;
    }

    @Override
    public String format(QueryResult result)
            throws Exception {

        NumericQuery query =
                (NumericQuery) result.getQuery();

        NumericResultPayload payload =
                (NumericResultPayload) result.getPayload();

        if (payload.results().isEmpty()) {
            return "No data available.";
        }

        StationSummary station =
                (StationSummary) payload.results().getFirst();

        double value = query.getValue();

        return llmClient.generate("""
                Answer in one sentence.

                Station: %s
                Metric: %s
                Value: %.2f %s
                """.formatted(
                station.getStation(),
                query.getMetric(),
                value,
                query.getMetric().getUnit()
        ));
    }
}