package com.example.pokemon_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pokemon_service.client.PokeApiClientWrapper;
import com.example.pokemon_service.dto.CreatePokemonRequest;
import com.example.pokemon_service.dto.CreatePokemonResponse;
import com.example.pokemon_service.dto.PokemonClientResponse;
import com.example.pokemon_service.dto.Stat;
import com.example.pokemon_service.exception.InvalidPokemonException;
import com.example.pokemon_service.exception.InvalidTrainerException;
import com.example.pokemon_service.exception.PokeApiClientException;
import com.example.pokemon_service.exception.PokemonNotFoundException;
import com.example.pokemon_service.exception.TrainerNotFoundException;
import com.example.pokemon_service.model.Genre;
import com.example.pokemon_service.model.Pokemon;
import com.example.pokemon_service.model.PokemonLocation;
import com.example.pokemon_service.model.PokemonStat;
import com.example.pokemon_service.model.StatType;
import com.example.pokemon_service.model.Trainer;
import com.example.pokemon_service.repository.PokemonRepository;
import com.example.pokemon_service.repository.TrainerRepository;

@Service
public class PokemonService {

	private final PokemonRepository pokemonRepository;
	private final TrainerRepository trainerRepository;
	private final PokeApiClientWrapper pokeApiClient;
	private final int teamCapacity;
	private final int pcBoxCapacity;

	public PokemonService(PokemonRepository pokemonRepository, TrainerRepository trainerRepository,
			PokeApiClientWrapper pokeApiClient,
			@Value("${pokemon.team.max-count}") int teamCapacity,
			@Value("${pokemon.pc-box.max-count}") int pcBoxCapacity) {
		this.pokemonRepository = pokemonRepository;
		this.trainerRepository = trainerRepository;
		this.pokeApiClient = pokeApiClient;
		this.teamCapacity = teamCapacity;
		this.pcBoxCapacity = pcBoxCapacity;
	}

	@Transactional
	public CreatePokemonResponse createPokemon(CreatePokemonRequest request) {
		final UUID trainerId;
		try {
			trainerId = UUID.fromString(request.trainerId());
		} catch (IllegalArgumentException exception) {
			throw new InvalidTrainerException("Invalid trainer id");
		}

		Pokemon pokemon = mapCreatePokemonRequestToPokemon(request);

		pokemon.validateStats();

		Trainer trainer = trainerRepository.findById(trainerId).orElse(null);
		if (trainer == null) {
			throw new TrainerNotFoundException("Trainer not found");
		}
		pokemon.setTrainer(trainer);

		validatePokemonAgainstAPI(pokemon);

		if (count(trainer.getTeamCount()) < teamCapacity) {
			pokemon.setLocation(PokemonLocation.TEAM);
			pokemon.setTeamSlot(nextTeamSlot(trainer));
			trainer.setTeamCount(count(trainer.getTeamCount()) + 1);
		} else if (count(trainer.getPcBoxCount()) < pcBoxCapacity) {
			pokemon.setLocation(PokemonLocation.PC_BOX);
			pokemon.setTeamSlot(null);
			trainer.setPcBoxCount(count(trainer.getPcBoxCount()) + 1);
		} else {
			throw new InvalidTrainerException("Cannot add more Pokemon to trainer's team or PC Box");
		}

		pokemonRepository.save(pokemon);
		return mapPokemonToCreatePokemonResponse(pokemon);

	}

	public Pokemon getPokemon(String id) {
		final UUID inputId;
		try {
			inputId = UUID.fromString(id.toString());
		} catch (IllegalArgumentException exception) {
			throw new InvalidTrainerException("Invalid pokemon id");
		}

		Pokemon pokemon = pokemonRepository.findById(inputId).orElse(null);
		if (pokemon == null) {
			throw new PokemonNotFoundException("Pokemon not found");
		}
		return pokemon;

	}

	private void validatePokemonAgainstAPI(Pokemon input) {
		PokemonClientResponse.Pokemon apiPokemon = pokeApiClient.getPokemon(input.getSpecies());
		if (apiPokemon == null) {
			throw new PokeApiClientException("Pokemon data not found");
		}
		if (input.getAbility() != null && apiPokemon.abilities().stream()
				.noneMatch(a -> a.ability().name().equalsIgnoreCase(input.getAbility()))) {
			throw new InvalidPokemonException("Invalid ability for the given species");
		}
		if (input.getHeldItem() != null && apiPokemon.heldItems().stream()
				.noneMatch(h -> h.item().name().equalsIgnoreCase(input.getHeldItem()))) {
			throw new InvalidPokemonException("Invalid held item for the given species");
		}
		if (input.getMovements() == null || input.getMovements().isEmpty()) {
			throw new InvalidPokemonException("At least one movement is required");
		}
		if (input.getMovements() != null && !input.getMovements().isEmpty()) {
			for (String move : input.getMovements()) {
				if (apiPokemon.moves().stream().noneMatch(m -> m.move().name().equalsIgnoreCase(move))) {
					throw new InvalidPokemonException("Invalid movement for the given species");
				}
			}
		}
	}

	private int count(Integer value) {
		return value == null ? 0 : value;
	}

	private int nextTeamSlot(Trainer trainer) {
		return count(trainer.getTeamCount()) + 1;
	}

	private Pokemon mapCreatePokemonRequestToPokemon(CreatePokemonRequest request) {
		Pokemon pokemon = new Pokemon(null, Genre.valueOf(request.genre()), request.ability(), request.isShiny(),
				PokemonLocation.TEAM, null, request.heldItem(), request.species(), null, request.movements(),
				request.captureLocation());

		List<PokemonStat> stats = new ArrayList<>();
		for (Stat statRequest : request.stats()) {
			stats.add(new PokemonStat(pokemon, StatType.valueOf(statRequest.name()), statRequest.value(),
					statRequest.effort()));
		}

		pokemon.setStats(stats);
		return pokemon;
	}

	private CreatePokemonResponse mapPokemonToCreatePokemonResponse(Pokemon pokemon) {
		return new CreatePokemonResponse()
				.setSpecies(pokemon.getSpecies())
				.setTrainerId(pokemon.getTrainer().getId())
				.setPokemonId(pokemon.getId())
				.setCaptureLocation(pokemon.getCaptureLocation())
				.setGenre(pokemon.getGenre().toString())
				.setAbility(pokemon.getAbility())
				.setShiny(pokemon.getIsShiny())
				.setLocation(pokemon.getLocation().toString())
				.setTeamSlot(pokemon.getTeamSlot())
				.setHeldItem(pokemon.getHeldItem())
				.setMovements(pokemon.getMovements())
				.setStats(pokemon.getStats());
	}
}
