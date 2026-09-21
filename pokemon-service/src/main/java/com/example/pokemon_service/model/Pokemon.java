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

	protected Pokemon() {
	}

	public Pokemon(Trainer trainer, Genre genre, String ability, Boolean isShiny, PokemonLocation location,
			Integer teamSlot, String heldItem, String species, List<PokemonStat> stats, Set<String> movements,
			String captureLocation) {
		this.trainer = trainer;
		this.genre = genre;
		this.ability = ability;
		this.isShiny = isShiny;
		this.location = location;
		this.teamSlot = teamSlot;
		this.heldItem = heldItem;
		this.species = species;
		this.stats = stats;
		this.movements = movements;
		this.captureLocation = captureLocation;
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

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public List<PokemonStat> getStats() {
		return stats;
	}

	public void setStats(List<PokemonStat> stats) {
		this.stats = stats;
	}

	public Set<String> getMovements() {
		return movements;
	}

	public void setMovements(Set<String> movements) {
		this.movements = movements;
	}

	public String getCaptureLocation() {
		return captureLocation;
	}

	public void setCaptureLocation(String captureLocation) {
		this.captureLocation = captureLocation;
	}

}
