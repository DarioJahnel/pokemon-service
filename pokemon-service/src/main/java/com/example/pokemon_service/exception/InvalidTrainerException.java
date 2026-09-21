package com.example.pokemon_service.exception;

public class InvalidTrainerException extends RuntimeException {
    public InvalidTrainerException(String message) {
        super(message);
    }
}
