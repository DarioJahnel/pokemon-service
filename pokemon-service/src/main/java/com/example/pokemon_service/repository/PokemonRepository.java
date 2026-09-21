package com.example.pokemon_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.pokemon_service.model.Pokemon;
import com.example.pokemon_service.model.PokemonLocation;

public interface PokemonRepository {

	Optional<Pokemon> findById(UUID id);

	Page<Pokemon> findTrainerPokemonsByLocation(UUID trainerId, PokemonLocation location, Pageable pageable);

	Pokemon save(Pokemon pokemon);
}
