package io.wahid.publication.ai.infra.ollama;

import io.wahid.publication.ai.dto.*;
import io.wahid.publication.ai.model.StationSummary;

import java.util.Comparator;
import java.util.List;

public interface LLMClient {
    String generate(String prompt) throws Exception;

    boolean isUp();

    boolean hasModel(String modelName);

    default NumericResult explainTrend(TrendResult trend) throws Exception {
        String explanation = buildExplanation(trend);
        StationSummary synthetic = StationSummary.builder()
                .station("TREND")
                .summaryText(explanation)
                .build();
        return new NumericResult(List.of(synthetic), List.of());
    }

    default NumericResult explainValue(NumericQuery query, GraphResult result) throws Exception {
        String prompt = "The " + query.getMetric() +
                " at " + result.station() +
                " is " + result.value() +
                " " + query.getMetric().getUnit() + ".";
        String explanation = generate(prompt);
        StationSummary synthetic = StationSummary.builder()
                .station("VALUE")
                .summaryText(explanation)
                .build();
        return new NumericResult(List.of(synthetic), List.of());
    }

    private String buildExplanation(TrendResult trend) throws Exception {
        TrendSummary s = summarize(trend);
        String prompt = """
                Answer in one concise paragraph.

                Facts:
                - Metric: %s
                - Period: %d to %d
                - Start value: %.2f
                - End value: %.2f
                - Peak value: %.2f in %d
                - Lowest value: %.2f in %d
                - Long-term slope: %.4f per year (%s)

                Guidelines:
                - Do NOT say values are zero unless stated.
                - Mention variability if present.
                - If slope is small, describe the trend as "slightly" or "overall".
                - Use plain language, not statistics jargon.
                """.formatted(
                s.metric(),
                s.startYear(),
                s.endYear(),
                s.startValue(),
                s.endValue(),
                s.maxValue(),
                s.maxYear(),
                s.minValue(),
                s.minYear(),
                s.slope(),
                s.direction().name().toLowerCase()
        );
        return generate(prompt);
    }

    private TrendSummary summarize(TrendResult trend) {
        var series = trend.series();
        YearValue first = series.getFirst();
        YearValue last = series.getLast();
        YearValue min = series.stream()
                .min(Comparator.comparingDouble(YearValue::value))
                .orElseThrow();
        YearValue max = series.stream()
                .max(Comparator.comparingDouble(YearValue::value))
                .orElseThrow();
        return new TrendSummary(
                trend.metric(),
                first.year(),
                first.value(),
                last.year(),
                last.value(),
                min.value(),
                min.year(),
                max.value(),
                max.year(),
                trend.slope(),
                trend.direction()
        );
    }
}
