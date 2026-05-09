package io.wahid.knowledge.service;

import io.wahid.knowledge.domain.MetricStats;
import io.wahid.knowledge.domain.SummaryFormatter;
import io.wahid.knowledge.domain.SummaryStats;

import java.util.Map;

public class WeatherSummaryFormatter implements SummaryFormatter<Void> {

    @Override
    public String format(SummaryStats<Void> stats) {

        StringBuilder sb = new StringBuilder(512);

        sb.append("DATASET: ").append(stats.dataset).append("\n");
        sb.append("LOCATION: ").append(stats.location).append("\n");
        sb.append("TIME_RANGE: ")
                .append(stats.startDate).append(" → ")
                .append(stats.endDate).append("\n");
        sb.append("ROWS: ").append(stats.rowCount).append("\n\n");

        sb.append("METRICS:\n");
        for (Map.Entry<String, MetricStats> e : stats.metrics.entrySet()) {
            MetricStats m = e.getValue();
            sb.append("- ")
                    .append(e.getKey())
                    .append(": min=").append(round(m.min))
                    .append(", max=").append(round(m.max))
                    .append(", avg=").append(round(m.avg))
                    .append("\n");
        }

        if (!stats.patterns.isEmpty()) {
            sb.append("\nPATTERNS:\n");
            for (String p : stats.patterns) {
                sb.append("- ").append(p).append("\n");
            }
        }

        if (!stats.outliers.isEmpty()) {
            sb.append("\nOUTLIERS:\n");
            for (String o : stats.outliers) {
                sb.append("- ").append(o).append("\n");
            }
        }

        return sb.toString();
    }

    private String round(double v) {
        return String.format("%.2f", v);
    }
}
