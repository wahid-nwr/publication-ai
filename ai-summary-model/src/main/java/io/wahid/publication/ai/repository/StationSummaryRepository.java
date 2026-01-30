package io.wahid.publication.ai.repository;

import io.wahid.publication.ai.model.StationSummary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.UUID;

public class StationSummaryRepository {

    private final EntityManagerFactory emf;

    public StationSummaryRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(StationSummary stationSummary) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(stationSummary);
            em.getTransaction().commit();
        }
    }

    public void updateEmbeddingId(UUID summaryId, String embeddingId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.createQuery("UPDATE StationSummary ss SET ss.embeddingId = :embeddingId WHERE ss.summaryId = :summaryId")
                    .setParameter("embeddingId", embeddingId)
                    .setParameter("summaryId", summaryId)
                    .executeUpdate();
        }
    }
}
