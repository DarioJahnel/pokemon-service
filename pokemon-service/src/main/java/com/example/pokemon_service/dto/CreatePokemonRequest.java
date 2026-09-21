package com.example.pokemon_service.dto;

import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePokemonRequest(
    @NotNull String species,
    @NotNull String trainerId,
    @NotNull String genre,
    @NotNull String ability,
    @NotNull Boolean isShiny,
    @NotNull String captureLocation,
    String heldItem,
    @NotNull @Size(min = 6) List<@Valid Stat> stats,
    @NotNull Set<@Valid String> movements
) {
} 