package com.example.pokemon_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record Stat(
                String name,
                @Min(1) Integer value,
                @Min(0) @Max(value = 252, message = "Effort value must be between 0 and 252") Integer effort,
                @Min(0) @Max(value = 31, message = "Genetic values must be between 0 and 31") Integer genetic) {
}
