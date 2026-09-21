package com.example.pokemon_service.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pokemon_service.dto.PokemonDetails;
import com.example.pokemon_service.model.PokemonLocation;
import com.example.pokemon_service.service.PokemonService;

import jakarta.validation.constraints.Min;

@RestController
@Validated
@RequestMapping("/trainers")
public class TrainerController {

    private final PokemonService pokemonService;

    public TrainerController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/{trainerId}/pokemon")
    public ResponseEntity<Page<PokemonDetails>> getTrainerPokemon(@PathVariable UUID trainerId,
            @RequestParam PokemonLocation type,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page number must be a positive integer") Integer pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be a positive integer") Integer pageSize) {
        return ResponseEntity.ok().body(pokemonService.getTrainerPokemon(trainerId, type, pageNumber, pageSize));
    }
}
