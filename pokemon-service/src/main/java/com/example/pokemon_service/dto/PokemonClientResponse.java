package com.example.pokemon_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PokemonClientResponse {

    public record Pokemon(
        int id,
        String name,
        int height,
        int weight,
        List<AbilitySlot> abilities,
        @JsonProperty("held_items") List<HeldItem> heldItems,
        List<MoveSlot> moves,
        NamedResource species,
        List<TypeSlot> types
    ) {}

    public record NamedResource(
        String name,
        String url
    ) {}

    public record AbilitySlot(
        @JsonProperty("is_hidden") boolean isHidden,
        int slot,
        NamedResource ability
    ) {}

    public record HeldItem(
        NamedResource item,
        @JsonProperty("version_details") List<VersionDetail> versionDetails
    ) {}

    public record VersionDetail(
        int rarity,
        NamedResource version
    ) {}

    public record MoveSlot(
        NamedResource move,
        @JsonProperty("version_group_details") List<VersionGroupDetail> versionGroupDetails
    ) {}

    public record VersionGroupDetail(
        @JsonProperty("level_learned_at") int levelLearnedAt,
        @JsonProperty("version_group") NamedResource versionGroup,
        @JsonProperty("move_learn_method") NamedResource moveLearnMethod,
        Integer order
    ) {}

    public record StatSlot(
        @JsonProperty("base_stat") int baseStat,
        int effort,
        NamedResource stat
    ) {}

    public record TypeSlot(
        int slot,
        NamedResource type
    ) {}
}