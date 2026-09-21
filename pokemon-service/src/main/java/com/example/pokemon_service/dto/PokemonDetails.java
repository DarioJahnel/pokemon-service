package com.example.pokemon_service.dto;

import java.util.List;

public record PokemonDetails(
        PokemonDTO pokemonData,
        Integer weigth,
        Integer height,
        List<String> types) {

}
