package io.wahid.publication.ai.repository;

import io.wahid.publication.ai.model.StationYearMetric;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class StationYearMetricRepository {

    private final EntityManagerFactory emf;

    public StationYearMetricRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public StationYearMetric save(StationYearMetric stationYearMetric) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            stationYearMetric = em.merge(stationYearMetric);
            em.getTransaction().commit();
        }
        return stationYearMetric;
    }

    public List<StationYearMetric> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT DISTINCT s FROM StationYearMetric s", StationYearMetric.class)
                    .getResultList();
        }
    }
}
