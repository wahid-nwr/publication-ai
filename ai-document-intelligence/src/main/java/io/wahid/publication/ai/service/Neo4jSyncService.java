package io.wahid.publication.ai.service;

import io.wahid.publication.ai.model.StationSummary;
import io.wahid.publication.ai.model.StationYearMetric;
import io.wahid.publication.ai.repository.StationSummaryRepository;
import io.wahid.publication.ai.repository.StationYearMetricRepository;
import io.wahid.publication.ai.util.JpaUtil;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.wahid.publication.ai.config.NumericMetric.*;

public class Neo4jSyncService {

    private final StationSummaryRepository repo;
    private final StationYearMetricRepository yearMetricRepository;
    private final Driver neo4j;

    public Neo4jSyncService(StationSummaryRepository repo, Driver driver) {
        this.repo = repo;
        this.yearMetricRepository = new StationYearMetricRepository(JpaUtil.getEntityManagerFactory());
        this.neo4j = driver;
    }

    public void sync() {
        try (Session session = neo4j.session()) {
            for (StationSummary s : repo.findAll()) {
                Map<String, Double> metrics = Map.of(
                        avgRainfall.name(), s.getAvgRainfall(),
                        totalRainfall.name(), s.getTotalRainfall(),
                        avgSunshine.name(), s.getAvgSunshine(),
                        avgHumidity.name(), s.getAvgHumidity(),
                        avgTemperature.name(), s.getAvgTemperature(),
                        minTemperature.name(), s.getMinTemperature(),
                        maxTemperature.name(), s.getMaxTemperature()
                );

                for (var entry : metrics.entrySet()) {
                    session.run("""
                                    MERGE (st:Station {name: $station})
                                    MERGE (sm:Summary {
                                        station: $station,
                                        metric: $metric
                                    })
                                    SET sm.value = $value,
                                        sm.startYear = $start,
                                        sm.endYear = $end
                                    MERGE (st)-[:HAS_SUMMARY]->(sm)
                                    """,
                            Map.of(
                                    "station", s.getStation(),
                                    "metric", entry.getKey(),
                                    "value", entry.getValue(),
                                    "start", s.getStartYear(),
                                    "end", s.getEndYear()
                            )
                    );
                }
            }
            List<Map<String, Object>> rows = new ArrayList<>();
            int batchSize = 1000;
            for (StationYearMetric metric : yearMetricRepository.findAll()) {
                rows.add(Map.of(
                        "station", metric.getYearMetricId().getStation(),
                        "year", metric.getYearMetricId().getYear(),
                        "metric", metric.getYearMetricId().getMetric(),
                        "value", metric.getValue()
                ));

                if (rows.size() == batchSize) {
                    writeYearMetrics(rows);
                    rows.clear();
                }
            }
            if (!rows.isEmpty()) {
                writeYearMetrics(rows);
            }
        }
    }

    public void writeYearMetrics(List<Map<String, Object>> rows) {

        String cypher = """
                UNWIND $rows AS row
                MERGE (s:Station {name: row.station})
                MERGE (y:Year {year: row.year})
                MERGE (m:Metric {name: row.metric})
                MERGE (s)-[:HAS_YEAR]->(y)
                MERGE (y)-[hm:HAS_METRIC]->(m)
                SET hm.value = row.value
                """;

        try (Session session = neo4j.session()) {
            session.executeWrite(tx -> {
                tx.run(cypher, Map.of("rows", rows)).consume();
                return null;
            });
        }
    }
}
