package com.example.pokemon_service.exception;

public class InvalidPokemonException extends RuntimeException {
    public InvalidPokemonException(String message) {
        super(message);
    }

}
