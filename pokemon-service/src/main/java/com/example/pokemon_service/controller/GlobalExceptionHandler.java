package com.example.pokemon_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.pokemon_service.exception.InvalidPokemonException;
import com.example.pokemon_service.exception.InvalidTrainerException;
import com.example.pokemon_service.exception.PokeApiClientException;
import com.example.pokemon_service.exception.PokemonNotFoundException;
import com.example.pokemon_service.exception.TrainerNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final String MESSAGE_KEY = "message";

        @ExceptionHandler(InvalidPokemonException.class)
        public ResponseEntity<Map<String, String>> handleInvalidPokemon(InvalidPokemonException ex) {
                String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                                ? ex.getMessage()
                                : "Invalid Pokemon request";

                return ResponseEntity.badRequest()
                                .body(Map.of(MESSAGE_KEY, message));
        }

        @ExceptionHandler(PokemonNotFoundException.class)
        public ResponseEntity<Map<String, String>> handlePokemonNotFound(PokemonNotFoundException ex) {
                String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                                ? ex.getMessage()
                                : "Pokemon not found";

                return ResponseEntity.badRequest()
                                .body(Map.of(MESSAGE_KEY, message));
        }

        @ExceptionHandler(PokeApiClientException.class)
        public ResponseEntity<Map<String, String>> handlePokeApiClientError(PokeApiClientException ex) {
                String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                                ? ex.getMessage()
                                : "Error occurred while fetching Pokemon data";

                return ResponseEntity.badRequest()
                                .body(Map.of(MESSAGE_KEY, message));
        }

        @ExceptionHandler(InvalidTrainerException.class)
        public ResponseEntity<Map<String, String>> handleInvalidTrainer(InvalidTrainerException ex) {
                String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                                ? ex.getMessage()
                                : "Invalid trainer request";

                return ResponseEntity.badRequest()
                                .body(Map.of(MESSAGE_KEY, message));
        }

        @ExceptionHandler(TrainerNotFoundException.class)
        public ResponseEntity<Map<String, String>> handleTrainerNotFound(TrainerNotFoundException ex) {
                String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                                ? ex.getMessage()
                                : "Trainer not found";

                return ResponseEntity.badRequest()
                                .body(Map.of(MESSAGE_KEY, message));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
                List<String> messages = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getDefaultMessage())
                                .toList();

                return ResponseEntity.badRequest()
                                .body(Map.of(MESSAGE_KEY, messages.toString()));
        }
}
