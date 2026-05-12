package io.wahid.knowledge.infrastructure.graph.neo4j.query.impl;

import io.wahid.knowledge.application.core.query.dto.GraphResult;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import java.util.Map;

public class Neo4jQueryService {

    private final Driver driver;

    public Neo4jQueryService(Driver driver) {
        this.driver = driver;
    }

    public GraphResult findMax(String metric) {

        try (Session session = driver.session()) {

            Record metricResult = session.run("""
            MATCH (s:Station)-[r:HAS_METRIC]->(:Metric {name: $metric})
            WITH s, r
            ORDER BY r.value DESC
            WITH s.name AS station,
                r.value AS value,
                r.startYear AS startYear,
                r.endYear AS endYear,
                row_number() OVER () AS rank
            RETURN station, value, startYear, endYear, rank
            LIMIT 1
            """, Map.of("metric", metric)
            ).single();

            return new GraphResult(
                    metricResult.get("station").asString(),
                    metric,
                    metricResult.get("value").asDouble(),
                    metricResult.get("startYear").asInt(),
                    metricResult.get("endYear").asInt(),
                    metricResult.get("rank").asInt()
            );
        }
    }

}
