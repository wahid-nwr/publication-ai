package io.wahid.knowledge.infrastructure.graph.neo4j;

import org.neo4j.driver.Record;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Neo4jGraphClient implements AutoCloseable {

    private final Driver driver;

    public Neo4jGraphClient(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(
                uri,
                AuthTokens.basic(user, password)
        );
        driver.verifyConnectivity();
        System.out.println("Connection established.");
    }

    public Driver getDriver() {
        return driver;
    }

    public <T> List<T> read(
            String cypher,
            Map<String, Object> params,
            Function<Record, T> mapper
    ) {

        try (Session session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            return session.executeRead(tx -> {
                List<T> results = new ArrayList<>();
                Result result = tx.run(cypher, params);
                while (result.hasNext()) {
                    results.add(mapper.apply(result.next()));
                }
                return results;
            });
        }
    }

    @Override
    public void close() {
        driver.close();
    }
}
