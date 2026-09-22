package com.example.pokemon_service.dto;

import com.example.pokemon_service.model.StatType;

public record NatureClientResponse(
        String name,
        StatType increasedStat,
        StatType decreasedStat) {
}
