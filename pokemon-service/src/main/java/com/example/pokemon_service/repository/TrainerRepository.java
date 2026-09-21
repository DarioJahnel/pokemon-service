package com.example.pokemon_service.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.pokemon_service.model.Trainer;

public interface TrainerRepository {

	Optional<Trainer> findById(UUID id);
}