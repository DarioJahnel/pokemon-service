package com.example.pokemon_service.model;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity 
public class Trainer {

    @Id 
    @GeneratedValue 
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;
    private Integer teamCount;
    private Integer pcBoxCount;

	protected Trainer() {
	}

	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public Integer getTeamCount() {
		return teamCount;
	}
	public void setTeamCount(Integer teamCount) {
		this.teamCount = teamCount;
	}
	public Integer getPcBoxCount() {
		return pcBoxCount;
	}
	public void setPcBoxCount(Integer pcBoxCount) {
		this.pcBoxCount = pcBoxCount;
	}

    
}
