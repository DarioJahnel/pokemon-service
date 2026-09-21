package com.example.pokemon_service.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.pokemon_service.model.PokemonStat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PokemonDTO {
        private String species;
        private UUID trainerId;
        private UUID pokemonId;
        private String captureLocation;
        private String genre;
        private String ability;
        @JsonProperty("isShiny")
        private Boolean isShiny;
        private String location;
        private Integer teamSlot;
        private String heldItem;
        private Set<String> movements;
        private List<Stat> stats = new ArrayList<>();

        public String getSpecies() {
                return species;
        }

        public PokemonDTO setSpecies(String species) {
                this.species = species;
                return this;
        }

        public UUID getTrainerId() {
                return trainerId;
        }

        public PokemonDTO setTrainerId(UUID trainerId) {
                this.trainerId = trainerId;
                return this;
        }

        public UUID getPokemonId() {
                return pokemonId;
        }

        public PokemonDTO setPokemonId(UUID pokemonId) {
                this.pokemonId = pokemonId;
                return this;
        }

        public String getCaptureLocation() {
                return captureLocation;
        }

        public PokemonDTO setCaptureLocation(String captureLocation) {
                this.captureLocation = captureLocation;
                return this;
        }

        public String getGenre() {
                return genre;
        }

        public PokemonDTO setGenre(String genre) {
                this.genre = genre;
                return this;
        }

        public String getAbility() {
                return ability;
        }

        public PokemonDTO setAbility(String ability) {
                this.ability = ability;
                return this;
        }

        public Boolean getShiny() {
                return isShiny;
        }

        public PokemonDTO setShiny(Boolean shiny) {
                isShiny = shiny;
                return this;
        }

        public String getLocation() {
                return location;
        }

        public PokemonDTO setLocation(String location) {
                this.location = location;
                return this;
        }

        public Integer getTeamSlot() {
                return teamSlot;
        }

        public PokemonDTO setTeamSlot(Integer teamSlot) {
                this.teamSlot = teamSlot;
                return this;
        }

        public String getHeldItem() {
                return heldItem;
        }

        public PokemonDTO setHeldItem(String heldItem) {
                this.heldItem = heldItem;
                return this;
        }

        public Set<String> getMovements() {
                return movements;
        }

        public PokemonDTO setMovements(Set<String> movements) {
                this.movements = movements;
                return this;
        }

        public List<Stat> getStats() {
                return stats;
        }

        public PokemonDTO setStats(List<PokemonStat> stats) {
                for (PokemonStat stat : stats) {
                        this.stats.add(new Stat(stat.getName().toString(), stat.getValue(), stat.getEffort(),
                                        stat.getGenetic()));
                }
                return this;
        }
}
