package io.wahid.publication.ai.model;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MAGAZINE")
@Access(AccessType.FIELD)
public class WeatherDetails {
}
