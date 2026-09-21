package com.example.pokemon_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.pokemon_service.model.Trainer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Optional<Trainer> findById(UUID id) {
		return Optional.ofNullable(entityManager.find(Trainer.class, id, LockModeType.PESSIMISTIC_WRITE));
	}
}