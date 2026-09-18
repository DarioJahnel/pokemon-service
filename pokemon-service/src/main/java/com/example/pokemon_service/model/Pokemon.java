package com.example.pokemon_service.model;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity 
public class Pokemon {
    @Id 
    @GeneratedValue 
    @UuidGenerator(style = Style.VERSION_7)
    private UUID id;

    @OneToMany(mappedBy = "trainer_id")
    private Trainer trainer;

    @OneToMany(
        mappedBy = "pokemon",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PokemonStat> stats;
    
    private Genre genre;
    private String ability;
    private Boolean isShiny;
    private PokemonLocation location;
    private Integer teamSlot;
    private String heldItem;

    
	public Pokemon(Trainer trainer, Genre genre, String ability, Boolean isShiny, PokemonLocation location,
			Integer teamSlot, String heldItem) {
		this.trainer = trainer;
		this.genre = genre;
		this.ability = ability;
		this.isShiny = isShiny;
		this.location = location;
		this.teamSlot = teamSlot;
		this.heldItem = heldItem;
	}

    public validateStats() {
        // Validate all stats are present

        // Validate maximum effort sum
    }

	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public Trainer getTrainer() {
		return trainer;
	}
	public void setTrainer(Trainer trainer) {
		this.trainer = trainer;
	}
	public Genre getGenre() {
		return genre;
	}
	public void setGenre(Genre genre) {
		this.genre = genre;
	}
	public String getAbility() {
		return ability;
	}
	public void setAbility(String ability) {
		this.ability = ability;
	}
	public Boolean getIsShiny() {
		return isShiny;
	}
	public void setIsShiny(Boolean isShiny) {
		this.isShiny = isShiny;
	}
	public PokemonLocation getLocation() {
		return location;
	}
	public void setLocation(PokemonLocation location) {
		this.location = location;
	}
	public Integer getTeamSlot() {
		return teamSlot;
	}
	public void setTeamSlot(Integer teamSlot) {
		this.teamSlot = teamSlot;
	}
	public String getHeldItem() {
		return heldItem;
	}
	public void setHeldItem(String heldItem) {
		this.heldItem = heldItem;
	}

}
