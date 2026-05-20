package io.wahid.knowledge.application.core.query.result;

import io.wahid.knowledge.application.core.query.dto.GraphResult;
import io.wahid.knowledge.application.core.query.dto.TrendResult;
import io.wahid.knowledge.application.domain.weather.query.model.NumericMetric;
import io.wahid.knowledge.application.domain.weather.query.model.NumericQueryType;
import io.wahid.knowledge.domain.query.result.QueryResultPayload;

import java.util.List;

public class NumericResultPayload<T> implements QueryResultPayload<T> {

    private NumericQueryType type;

    private NumericMetric metric;

    private final List<T> results;

    private TrendResult trend;

    private GraphResult value;

    private int total;

    public NumericResultPayload(NumericQueryType type,
                                NumericMetric metric,
                                List<T> results,
                                TrendResult trend,
                                GraphResult value,
                                int total
    ) {
        this.type = type;
        this.metric = metric;
        this.results = results;
        this.trend = trend;
        this.value = value;
        this.total = total;
    }

    @Override
    public List<T> results() {
        return results;
    }

    public NumericQueryType getType() {
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