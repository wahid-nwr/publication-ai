package io.wahid.knowledge.repository;

import io.wahid.knowledge.model.StationYearMetric;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

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

    public void removeAll() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM StationYearMetric").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
