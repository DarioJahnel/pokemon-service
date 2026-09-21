package com.example.pokemon_service.model;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

import com.example.pokemon_service.exception.InvalidPokemonException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Pokemon {
	@Id
	@GeneratedValue
	@UuidGenerator(style = Style.VERSION_7)
	private UUID id;

	private String species;

	@ManyToOne
	@JoinColumn(name = "trainer_id", nullable = false)
	private Trainer trainer;

	@OneToMany(mappedBy = "pokemon", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PokemonStat> stats = new java.util.ArrayList<>();

	@Enumerated(EnumType.STRING)
	private Genre genre;
	private String ability;
	private Boolean isShiny;
	@Enumerated(EnumType.STRING)
	private PokemonLocation location;
	private Integer teamSlot;
	private String heldItem;
	private Set<String> movements = new java.util.HashSet<>();
	private String captureLocation;

	public Pokemon() {
	}

	public void validateStats() {
		// Validate all stats are present
		if (stats.stream()
				.map(PokemonStat::getName)
				.distinct()
				.count() != stats.size()) {
			throw new InvalidPokemonException("Stats must be unique and 6 total stats");
		}

		// Validate maximum effort sum
		int totalEffort = stats.stream()
				.mapToInt(PokemonStat::getEffort)
				.sum();

		if (totalEffort > 510) {
			throw new InvalidPokemonException("Total effort must not exceed 510");
		}
	}

	public UUID getId() {
		return id;
	}

	public Pokemon setId(UUID id) {
		this.id = id;
		return this;
	}

	public String getSpecies() {
		return species;
	}

	public Pokemon setSpecies(String species) {
		this.species = species;
		return this;
	}

	public Trainer getTrainer() {
		return trainer;
	}

	public Pokemon setTrainer(Trainer trainer) {
		this.trainer = trainer;
		return this;
	}

	public List<PokemonStat> getStats() {
		return stats;
	}

	public Pokemon setStats(List<PokemonStat> stats) {
		this.stats = stats;
		return this;
	}

	public Genre getGenre() {
		return genre;
	}

	public Pokemon setGenre(Genre genre) {
		this.genre = genre;
		return this;
	}

	public String getAbility() {
		return ability;
	}

	public Pokemon setAbility(String ability) {
		this.ability = ability;
		return this;
	}

	public Boolean getIsShiny() {
		return isShiny;
	}

	public Pokemon setShiny(Boolean shiny) {
		isShiny = shiny;
		return this;
	}

	public PokemonLocation getLocation() {
		return location;
	}

	public Pokemon setLocation(PokemonLocation location) {
		this.location = location;
		return this;
	}

	public Integer getTeamSlot() {
		return teamSlot;
	}

	public Pokemon setTeamSlot(Integer teamSlot) {
		this.teamSlot = teamSlot;
		return this;
	}

	public String getHeldItem() {
		return heldItem;
	}

	public Pokemon setHeldItem(String heldItem) {
		this.heldItem = heldItem;
		return this;
	}

	public Set<String> getMovements() {
		return movements;
	}

	public Pokemon setMovements(Set<String> movements) {
		this.movements = movements;
		return this;
	}

	public String getCaptureLocation() {
		return captureLocation;
	}

	public Pokemon setCaptureLocation(String captureLocation) {
		this.captureLocation = captureLocation;
		return this;
	}
}
