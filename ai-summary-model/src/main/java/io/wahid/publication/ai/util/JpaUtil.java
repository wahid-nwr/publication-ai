package io.wahid.publication.ai.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JpaUtil {

    private static final String PERSISTENCE_UNIT_NAME = "optimizely-unit";

    private JpaUtil() {}

    private static EntityManagerFactory emf;

    /*private static final class EmfHolder {
        private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return EmfHolder.emf;
    }*/

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {

            Map<String, Object> overrides = new HashMap<>();

            putIfEnv(overrides, "JPA_JDBC_URL", "jakarta.persistence.jdbc.url");
            putIfEnv(overrides, "JPA_JDBC_USER", "jakarta.persistence.jdbc.user");
            putIfEnv(overrides, "JPA_JDBC_PASSWORD", "jakarta.persistence.jdbc.password");
            putIfEnv(overrides, "JPA_JDBC_DRIVER", "jakarta.persistence.jdbc.driver");

            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, overrides);
        }
        return emf;
    }

    private static void putIfEnv(Map<String, Object> map, String envName, String jpaName) {
        String val = System.getenv(envName);
        if (val != null && !val.isEmpty()) {
            map.put(jpaName, val);
        }
    }

    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

