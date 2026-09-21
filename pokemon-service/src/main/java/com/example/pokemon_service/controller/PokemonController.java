package com.example.pokemon_service.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pokemon_service.dto.CreatePokemonRequest;
import com.example.pokemon_service.dto.CreatePokemonResponse;
import com.example.pokemon_service.model.Pokemon;
import com.example.pokemon_service.service.PokemonService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pokemon")
public class PokemonController {

	private final PokemonService pokemonService;

	public PokemonController(PokemonService pokemonService) {
		this.pokemonService = pokemonService;
	}

	@PostMapping
	public ResponseEntity<CreatePokemonResponse> createPokemon(@Valid @RequestBody CreatePokemonRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(pokemonService.createPokemon(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Pokemon> getPokemon(@PathVariable String id) {
		// Implementation for retrieving a specific Pokemon
		return ResponseEntity.ok().body(pokemonService.getPokemon(id));
	}
}
