package io.wahid.knowledge.application.core.query.result;

import io.wahid.knowledge.application.core.query.dto.GraphResult;
import io.wahid.knowledge.application.core.query.dto.TrendResult;
import io.wahid.knowledge.application.domain.weather.query.model.NumericMetric;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQueryType;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.domain.query.result.QueryResultPayload;
import io.wahid.knowledge.model.StationSummary;

import java.util.List;

public class NumericExecutionResult extends QueryResult
        implements QueryResultPayload {

    private NumericQueryType type;

    private NumericMetric metric;

    private List<StationSummary> stations;

    private TrendResult trend;

    private GraphResult value;

    private int total;

    public NumericExecutionResult(
            NumericQueryType type,
            NumericMetric metric,
            List<StationSummary> stations,
            TrendResult trend,
            GraphResult value,
            int total
    ) {
        this.type = type;
        this.metric = metric;
        this.stations = stations;
        this.trend = trend;
        this.value = value;
        this.total = total;
    }

    @Override
    public List<?> results() {
        return stations;
    }

    public NumericQueryType getNumericType() {
        return type;
    }

    public void setType(NumericQueryType type) {
        this.type = type;
    }

    public NumericMetric getMetric() {
        return metric;
    }

    public void setMetric(NumericMetric metric) {
        this.metric = metric;
    }

    public List<StationSummary> getStations() {
        return stations;
    }

    public void setStations(List<StationSummary> stations) {
        this.stations = stations;
    }

    public TrendResult getTrend() {
        return trend;
    }

    public void setTrend(TrendResult trend) {
        this.trend = trend;
    }

    public GraphResult getValue() {
        return value;
    }

    public void setValue(GraphResult value) {
        this.value = value;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}