package io.wahid.publication.ai.repository;

import io.wahid.publication.ai.model.StationSummary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.UUID;

public class StationSummaryRepository {

    private final EntityManagerFactory emf;

    public StationSummaryRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public StationSummary save(StationSummary stationSummary) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            stationSummary = em.merge(stationSummary);
            em.getTransaction().commit();
        }
        return stationSummary;
    }

    public StationSummary findTopByOrderByTotalRainfallDesc() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT s FROM StationSummary s ORDER BY s.totalRainfall DESC LIMIT 1", StationSummary.class)
                    .getSingleResult();
        }
    }

    public List<StationSummary> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT DISTINCT s FROM StationSummary s", StationSummary.class)
                    .getResultList();
        }
    }

    public List<String> findAllStations() {
        return findAll().stream().map(StationSummary::getStation).toList();
    }

    public void updateEmbeddingId(UUID summaryId, String embeddingId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("UPDATE StationSummary ss SET ss.embeddingId = :embeddingId WHERE ss.summaryId = :summaryId")
                    .setParameter("embeddingId", embeddingId)
                    .setParameter("summaryId", summaryId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<StationSummary> findByStationIn(List<String> stations) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT DISTINCT s FROM StationSummary s WHERE s.station IN (:stations)", StationSummary.class)
                    .setParameter("stations", stations)
                    .getResultList();
        }
    }
}
