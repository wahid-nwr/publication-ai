package io.wahid.knowledge.infrastructure.graph.neo4j.query.impl;

import io.wahid.knowledge.application.core.query.handler.numeric.NumericMetric;
import io.wahid.knowledge.domain.query.TrendDirection;
import io.wahid.knowledge.application.core.query.dto.GraphResult;
import io.wahid.knowledge.application.core.query.dto.TrendResult;
import io.wahid.knowledge.application.domain.weather.model.YearValue;
import io.wahid.knowledge.infrastructure.graph.neo4j.Neo4jGraphClient;
import io.wahid.knowledge.util.TrendMath;
import org.neo4j.driver.Session;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Neo4jWeatherRepository {

    private static final Logger LOGGER = Logger.getLogger(Neo4jWeatherRepository.class.getName());
    private final Neo4jGraphClient neo4jGraphClient;

    public Neo4jWeatherRepository(Neo4jGraphClient neo4jGraphClient) {
        this.neo4jGraphClient = neo4jGraphClient;
    }

    public GraphResult findTopByMetricAsc(String metric) {
        return querySingle(metric, "ASC", 1).getFirst();
    }

    public GraphResult findTopByMetricDesc(String metric) {
        return querySingle(metric, "DESC", 1).getFirst();
    }

    public List<GraphResult> findBottomKByMetric(String metric, int k) {
        return querySingle(metric, "ASC", k);
    }

    public List<GraphResult> findTopKByMetricDesc(String metric, int k) {
        return querySingle(metric, "DESC", k);
    }

    private List<GraphResult> querySingle(String metric, String order, int limit) {

        String cypher = """
                MATCH (s:Station)-[:HAS_SUMMARY]->(sm:Summary {metric: $metric})
                     WITH s, sm
                     ORDER BY sm.value %s  // smallest value first (for MIN); DESC for MAX
                     WITH collect({station: s.name, value: sm.value, startYear: sm.startYear, endYear: sm.endYear}) AS stations
                     UNWIND range(1, size(stations)) AS rank
                     WITH stations[rank - 1] AS st, rank
                     RETURN st.station AS station,
                            st.value AS value,
                            st.startYear AS startYear,
                            st.endYear AS endYear,
                            rank
                     ORDER BY rank
                     LIMIT $limit;
                """.formatted(order);

        LOGGER.log(Level.INFO, "neo4j cypher->{0}", cypher);
        try (Session session = neo4jGraphClient.getDriver().session()) {
            return session.run(cypher, Map.of(
                    "metric", metric,
                    "limit", limit
            )).list(r ->
                    new GraphResult(
                            r.get("station").asString(),
                            metric,
                            r.get("value").asDouble(),
                            r.get("startYear").asInt(),
                            r.get("endYear").asInt(),
                            r.get("rank").asInt()
                    )
            );
        }
    }

    public List<YearValue> getMetricTrend(String station,
                                          String metric,
                                          int fromYear) {
        String cypher = """
                MATCH (s:Station {name: $station})
                      -[:HAS_YEAR]->(y:Year)
                      -[hm:HAS_METRIC]->(m:Metric {name: $metric})
                WHERE y.year >= $fromYear
                RETURN y.year AS year,
                       hm.value AS value
                ORDER BY y.year ASC
                """;

        System.out.println("station->" + station + ", metric->" + metric + ", fromyear->" + fromYear);
        Map<String, Object> params = Map.of(
                "station", station,
                "metric", metric,
                "fromYear", fromYear
        );

        return neo4jGraphClient.read(
                cypher,
                params,
                result -> new YearValue(
                        result.get("year").asInt(),
                        result.get("value").asDouble()
                ));
    }

    public TrendResult computeTrend(NumericMetric metric, List<YearValue> series, String station) {
        if (series.size() < 2) {
            return new TrendResult(TrendDirection.FLAT, 0.0, series, metric, station, 0, 0);
        }

        double[] years = series.stream()
                .mapToDouble(YearValue::year)
                .toArray();

        double[] values = series.stream()
                .mapToDouble(YearValue::value)
                .toArray();

        double slope = TrendMath.slope(years, values);

        TrendDirection direction = switch (Double.compare(slope, 0.0)) {
            case 1 -> TrendDirection.UP;   // slope > 0
            case -1 -> TrendDirection.DOWN; // slope < 0
            default -> TrendDirection.FLAT; // slope == 0
        };

        return new TrendResult(direction, slope, series, metric, station, (int) years[0], (int) years[years.length - 1]);
    }

    public GraphResult getMetricValue(String station, String metric) {
        String cypher = """
                     MATCH (s:Station {name: $station})
                          -[:HAS_YEAR]->(y:Year)
                          -[r:HAS_METRIC]->(m:Metric {name: $metric})
                     RETURN y.year AS year,
                          r.value AS value
                     ORDER BY y.year DESC
                     LIMIT 1
                 """;
        Map<String, Object> params = Map.of(
                "station", station,
                "metric", metric
        );
        List<GraphResult> graphResults = neo4jGraphClient.read(
                cypher,
                params,
                result -> new GraphResult(
                        station,
                        metric,
                        result.get("value").asDouble(),
                        result.get("year").asInt(),
                        result.get("year").asInt(),
                        1
                ));
        return graphResults.isEmpty() ? null : graphResults.getFirst();
    }
}
