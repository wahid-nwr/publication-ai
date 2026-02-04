package io.wahid.publication.ai.service;

import io.wahid.publication.ai.model.StationSummary;
import io.wahid.publication.ai.repository.StationSummaryRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

import static io.wahid.publication.ai.config.NumericMetric.*;

public class Neo4jSyncService {

    private final StationSummaryRepository repo;
    private final Driver neo4j;

    public Neo4jSyncService(StationSummaryRepository repo, Driver driver) {
        this.repo = repo;
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
        }
    }
}
