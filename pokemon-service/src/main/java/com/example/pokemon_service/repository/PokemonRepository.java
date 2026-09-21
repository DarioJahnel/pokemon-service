package com.example.pokemon_service.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.pokemon_service.model.Pokemon;

public interface PokemonRepository {

	Optional<Pokemon> findById(UUID id);

	Pokemon save(Pokemon pokemon);
}
