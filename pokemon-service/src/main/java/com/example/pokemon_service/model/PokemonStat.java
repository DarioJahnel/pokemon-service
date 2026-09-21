package com.example.pokemon_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PokemonStat {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pokemon_id", nullable = false)
	private Pokemon pokemon;

	@Enumerated(EnumType.STRING)
	private StatType name;

	@Column(name = "stat_value")
	private Integer value;

	private Integer effort;

	private Integer genetic;

	protected PokemonStat() {
	}

	public PokemonStat(Pokemon pokemon, StatType name, Integer value, Integer effort, Integer genetic) {
		this.pokemon = pokemon;
		this.name = name;
		this.value = value;
		this.effort = effort;
		this.genetic = genetic;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Pokemon getPokemon() {
		return pokemon;
	}

	public void setPokemon(Pokemon pokemon) {
		this.pokemon = pokemon;
	}

	public StatType getName() {
		return name;
	}

	public void setName(StatType name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getEffort() {
		return effort;
	}

	public void setEffort(Integer effort) {
		this.effort = effort;
	}

	public Integer getGenetic() {
		return genetic;
	}

	public void setGenetic(Integer genetic) {
		this.genetic = genetic;
	}

}
