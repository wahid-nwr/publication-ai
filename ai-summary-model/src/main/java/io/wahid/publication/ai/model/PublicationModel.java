package io.wahid.publication.ai.model;

import io.wahid.publication.ai.domain.Document;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "publication_type", discriminatorType = DiscriminatorType.STRING)
@Access(AccessType.FIELD)
public abstract class PublicationModel implements Document {
    @Column(nullable = false)
    private final String title;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    protected PublicationModel() {
        this.title = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublicationModel that = (PublicationModel) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }
}
