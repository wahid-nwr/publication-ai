package io.wahid.publication.ai.infra.ollama;

import io.wahid.publication.ai.dto.NumericResult;
import io.wahid.publication.ai.dto.TrendResult;
import io.wahid.publication.ai.model.StationSummary;

import java.util.List;

public interface LLMClient {
    String generate(String prompt) throws Exception;
    boolean isUp();
    boolean hasModel(String modelName);
    default NumericResult explainTrend(TrendResult trend) {

        String explanation = buildExplanation(trend);

        StationSummary synthetic = StationSummary.builder()
                .station("TREND")
                .summaryText(explanation)
                .build();

        return new NumericResult(List.of(synthetic));
    }

    private String buildExplanation(TrendResult trend) {

        String direction = switch (trend.direction()) {
            case UP -> "increasing";
            case DOWN -> "decreasing";
            case FLAT -> "stable";
        };

        return String.format(
                "The metric shows a %s trend over time with a slope of %.3f per year.",
                direction,
                trend.slope()
        );
    }
}
