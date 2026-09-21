package com.example.pokemon_service.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pokemon_service.dto.CreatePokemonRequest;
import com.example.pokemon_service.dto.EvolvePokemonRequest;
import com.example.pokemon_service.dto.PokemonDTO;
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
	public ResponseEntity<PokemonDTO> createPokemon(@RequestBody @Valid CreatePokemonRequest req) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(pokemonService.createPokemon(req));
	}

	@GetMapping("/{pokemonId}")
	public ResponseEntity<PokemonDTO> getPokemon(@PathVariable UUID pokemonId) {
		return ResponseEntity.ok().body(pokemonService.getPokemon(pokemonId));
	}

	@PatchMapping("/{pokemonId}/move")
	public ResponseEntity<PokemonDTO> movePokemon(@PathVariable UUID pokemonId) {
		return ResponseEntity.ok().body(pokemonService.movePokemon(pokemonId));
	}

	@PatchMapping("/{pokemonId}/evolve")
	public ResponseEntity<PokemonDTO> evolvePokemon(@PathVariable UUID pokemonId, @RequestBody EvolvePokemonRequest req) {
		return ResponseEntity.ok().body(pokemonService.evolvePokemon(pokemonId, req));
	}
}
