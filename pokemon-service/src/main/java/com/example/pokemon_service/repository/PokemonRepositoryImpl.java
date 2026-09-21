package com.example.pokemon_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.pokemon_service.model.Pokemon;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class PokemonRepositoryImpl implements PokemonRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Optional<Pokemon> findById(UUID id) {
		return Optional.ofNullable(entityManager.find(Pokemon.class, id));
	}

	@Override
	public Pokemon save(Pokemon pokemon) {
		entityManager.persist(pokemon);
		return pokemon;
	}
}
