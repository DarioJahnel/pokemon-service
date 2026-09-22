package com.example.pokemon_service.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePokemonRequest(
                @NotNull String species,
                @NotNull UUID trainerId,
                @NotNull String genre,
                @NotNull String ability,
                @NotNull Boolean isShiny,
                @NotNull String captureLocation,
                String heldItem,
                @NotNull @Size(min = 6, max = 6, message = "Stats must contain exactly 6 values") List<@Valid Stat> stats,
                @NotNull Set<@Valid String> movements,
                @NotNull String nature) {
}