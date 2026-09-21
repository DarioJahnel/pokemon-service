package com.example.pokemon_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.pokemon_service.model.Pokemon;
import com.example.pokemon_service.model.PokemonLocation;

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

	@Override
	public Page<Pokemon> findTrainerPokemonsByLocation(UUID trainerId, PokemonLocation location, Pageable pageable) {
		var query = entityManager.createQuery("""
				SELECT p
				FROM Pokemon p
				WHERE p.trainer.id = :trainerId
				  AND p.location = :location
				ORDER BY p.id
				""", Pokemon.class)
				.setParameter("trainerId", trainerId)
				.setParameter("location", location)
				.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize())
				.getResultList();

		long total = entityManager.createQuery("""
				SELECT COUNT(p)
				FROM Pokemon p
				WHERE p.trainer.id = :trainerId
				  AND p.location = :location
				""", Long.class)
				.setParameter("trainerId", trainerId)
				.setParameter("location", location)
				.getSingleResult();

		return new PageImpl<>(query, pageable, total);
	}
}
