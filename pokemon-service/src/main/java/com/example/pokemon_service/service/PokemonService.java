package com.example.pokemon_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pokemon_service.client.PokeApiClientWrapper;
import com.example.pokemon_service.dto.CreatePokemonRequest;
import com.example.pokemon_service.dto.EvolvePokemonRequest;
import com.example.pokemon_service.dto.NatureClientResponse;
import com.example.pokemon_service.dto.PokemonClientResponse;
import com.example.pokemon_service.dto.PokemonClientSpeciesResponse;
import com.example.pokemon_service.dto.PokemonDTO;
import com.example.pokemon_service.dto.PokemonDetails;
import com.example.pokemon_service.dto.Stat;
import com.example.pokemon_service.exception.InvalidPokemonException;
import com.example.pokemon_service.exception.InvalidTrainerException;
import com.example.pokemon_service.exception.PokeApiClientException;
import com.example.pokemon_service.exception.PokemonNotFoundException;
import com.example.pokemon_service.exception.TrainerNotFoundException;
import com.example.pokemon_service.model.Genre;
import com.example.pokemon_service.model.Nature;
import com.example.pokemon_service.model.Pokemon;
import com.example.pokemon_service.model.PokemonLocation;
import com.example.pokemon_service.model.PokemonStat;
import com.example.pokemon_service.model.StatType;
import com.example.pokemon_service.model.Trainer;
import com.example.pokemon_service.repository.NatureRepository;
import com.example.pokemon_service.repository.PokemonRepository;
import com.example.pokemon_service.repository.TrainerRepository;

@Service
public class PokemonService {

	private final PokemonRepository pokemonRepository;
	private final NatureRepository natureRepository;
	private final TrainerRepository trainerRepository;
	private final PokeApiClientWrapper pokeApiClient;
	private final int teamCapacity;
	private final int pcBoxCapacity;

	public PokemonService(PokemonRepository pokemonRepository, TrainerRepository trainerRepository,
			NatureRepository natureRepository,
			PokeApiClientWrapper pokeApiClient,
			@Value("${pokemon.team.max-count}") int teamCapacity,
			@Value("${pokemon.pc-box.max-count}") int pcBoxCapacity) {
		this.pokemonRepository = pokemonRepository;
		this.trainerRepository = trainerRepository;
		this.natureRepository = natureRepository;
		this.pokeApiClient = pokeApiClient;
		this.teamCapacity = teamCapacity;
		this.pcBoxCapacity = pcBoxCapacity;
	}

	@Transactional
	public PokemonDTO createPokemon(CreatePokemonRequest request) {
		Pokemon pokemon = mapCreatePokemonRequestToPokemon(request);

		pokemon.validateStats();

		pokemon.setNature(findNature(request.nature()));

		Trainer trainer = trainerRepository.findByIdForUpdate(request.trainerId()).orElse(null);
		if (trainer == null) {
			throw new TrainerNotFoundException("Trainer not found");
		}
		pokemon.setTrainer(trainer);

		validatePokemonAgainstAPI(pokemon);

		if (count(trainer.getTeamCount()) < teamCapacity) {
			pokemon.setLocation(PokemonLocation.TEAM);
			pokemon.setTeamSlot(findEmptyTeamSlot(trainer.getId()));
			trainer.setTeamCount(count(trainer.getTeamCount()) + 1);
		} else if (count(trainer.getPcBoxCount()) < pcBoxCapacity) {
			pokemon.setLocation(PokemonLocation.PC_BOX);
			pokemon.setTeamSlot(null);
			trainer.setPcBoxCount(count(trainer.getPcBoxCount()) + 1);
		} else {
			throw new InvalidTrainerException("Cannot add more Pokemon to trainer's team or PC Box");
		}

		natureRepository.save(pokemon.getNature());
		pokemonRepository.save(pokemon);
		return mapPokemonToPokemonDTO(pokemon);

	}

	public PokemonDTO getPokemon(UUID id) {
		Pokemon pokemon = pokemonRepository.findById(id).orElse(null);
		if (pokemon == null) {
			throw new PokemonNotFoundException("Pokemon not found");
		}
		return mapPokemonToPokemonDTO(pokemon);
	}

	public Page<PokemonDetails> getTrainerPokemon(UUID trainerId, PokemonLocation type, Integer pageNumber,
			Integer pageSize) {
		Page<PokemonDetails> response = pokemonRepository.findTrainerPokemonsByLocation(trainerId, type,
				PageRequest.of(pageNumber - 1, pageSize)).map(this::mapToPokemonDetails);
		if (response.isEmpty()) {
			throw new PokemonNotFoundException("No Pokemon found for the given trainer and location");
		}

		return response;
	}

	@Transactional
	public PokemonDTO movePokemon(UUID pokemonId) {
		Pokemon pokemon = pokemonRepository.findById(pokemonId).orElse(null);
		if (pokemon == null) {
			throw new PokemonNotFoundException("Pokemon not found");
		}

		// Find trainer to obtain lock
		UUID trainerId = pokemon.getTrainer().getId();
		Trainer trainer = trainerRepository.findByIdForUpdate(trainerId).orElse(null);
		if (trainer == null) {
			throw new TrainerNotFoundException("Trainer not found");
		}

		if (pokemon.getLocation() == PokemonLocation.TEAM) {
			if (count(trainer.getPcBoxCount()) >= pcBoxCapacity) {
				throw new InvalidPokemonException("Cannot move Pokemon to PC Box, capacity reached");
			}
			pokemon.setLocation(PokemonLocation.PC_BOX);
			pokemon.setTeamSlot(null);
			trainer.setTeamCount(count(trainer.getTeamCount()) - 1);
			trainer.setPcBoxCount(count(trainer.getPcBoxCount()) + 1);
		} else if (pokemon.getLocation() == PokemonLocation.PC_BOX) {
			if (count(trainer.getTeamCount()) >= teamCapacity) {
				throw new InvalidPokemonException("Cannot move Pokemon to Team, capacity reached");
			}
			pokemon.setLocation(PokemonLocation.TEAM);
			pokemon.setTeamSlot(findEmptyTeamSlot(trainer.getId()));
			trainer.setPcBoxCount(count(trainer.getPcBoxCount()) - 1);
			trainer.setTeamCount(count(trainer.getTeamCount()) + 1);
		} else {
			throw new InvalidPokemonException("Invalid Pokemon location type");
		}

		pokemonRepository.save(pokemon);
		return mapPokemonToPokemonDTO(pokemon);
	}

	@Transactional
	public PokemonDTO evolvePokemon(UUID pokemonId, EvolvePokemonRequest req) {
		Pokemon pokemon = pokemonRepository.findById(pokemonId).orElse(null);
		if (pokemon == null) {
			throw new PokemonNotFoundException("Pokemon not found");
		}

		if (pokemon.getTeamSlot() == null) {
			throw new InvalidPokemonException("Cannot evolve PC_BOX pokemon");
		}

		PokemonClientSpeciesResponse evolvedSpecies = pokeApiClient.getPokemonSpecies(req.newSpecies());
		if (evolvedSpecies == null) {
			throw new PokemonNotFoundException("Evolution species not found");
		}

		if (!pokemon.getSpecies().equalsIgnoreCase(evolvedSpecies.evolvesFrom())) {
			throw new InvalidPokemonException(
					String.format("%s cannot evolve to %s", pokemon.getSpecies(), req.newSpecies()));
		}

		PokemonClientResponse.Pokemon evolvedPokemon = pokeApiClient.getPokemon(req.newSpecies());
		if (evolvedPokemon == null) {
			throw new PokemonNotFoundException("Evolution pokemon not found");
		}

		pokemon.setSpecies(req.newSpecies());

		String newAbility = evolvedPokemon.abilities().isEmpty() ? ""
				: evolvedPokemon.abilities().get(0).ability().name();
		pokemon.setAbility(newAbility);

		return mapPokemonToPokemonDTO(pokemon);
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

	private Pokemon mapCreatePokemonRequestToPokemon(CreatePokemonRequest request) {
		Pokemon pokemon = new Pokemon()
				.setGenre(Genre.valueOf(request.genre()))
				.setAbility(request.ability())
				.setShiny(request.isShiny())
				.setLocation(PokemonLocation.TEAM)
				.setTeamSlot(null)
				.setHeldItem(request.heldItem())
				.setSpecies(request.species())
				.setMovements(request.movements())
				.setCaptureLocation(request.captureLocation());

		List<PokemonStat> stats = new ArrayList<>();
		for (Stat statRequest : request.stats()) {
			stats.add(new PokemonStat(pokemon, StatType.valueOf(statRequest.name()), statRequest.value(),
					statRequest.effort(), statRequest.genetic()));
		}

		pokemon.setStats(stats);
		return pokemon;
	}

	private PokemonDTO mapPokemonToPokemonDTO(Pokemon pokemon) {

		// Calculate stat buff/debuff depending on nature
		List<Stat> adjustedStats = pokemon.getStats().stream()
				.map(stat -> {
					int adjustedValue = stat.getValue();
					if (pokemon.getNature() != null && stat.getName() == pokemon.getNature().getIncreasedStat()) {
						adjustedValue = (int) Math.round(stat.getValue() * 1.10);
					} else if (pokemon.getNature() != null
							&& stat.getName() == pokemon.getNature().getDecreasedStat()) {
						adjustedValue = (int) Math.round(stat.getValue() * 0.90);
					}
					return new Stat(stat.getName().toString(), adjustedValue, stat.getEffort(), stat.getGenetic());
				})
				.toList();

		return new PokemonDTO()
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
				.setStats(adjustedStats)
				.setNature(pokemon.getNature().getName());
	}

	private PokemonDetails mapToPokemonDetails(Pokemon pokemon) {
		PokemonClientResponse.Pokemon apiPokemon = pokeApiClient.getPokemon(pokemon.getSpecies());

		if (apiPokemon == null) {
			throw new PokeApiClientException("Pokemon data not found");
		}

		return new PokemonDetails(
				mapPokemonToPokemonDTO(pokemon),
				apiPokemon.weight(),
				apiPokemon.height(),
				apiPokemon.types()
						.stream()
						.map(t -> t.type().name())
						.toList());
	}

	private Integer findEmptyTeamSlot(UUID trainerId) {
		List<Pokemon> pokemonList = pokemonRepository
				.findTrainerPokemonsByLocation(trainerId, PokemonLocation.TEAM, PageRequest.of(0, teamCapacity))
				.getContent();
		if (pokemonList.isEmpty()) {
			return 1;
		} else {
			List<Integer> occupiedSlots = pokemonList.stream()
					.map(Pokemon::getTeamSlot)
					.toList();
			for (int i = 1; i <= teamCapacity; i++) {
				if (!occupiedSlots.contains(i)) {
					return i;
				}
			}
			return null;
		}
	}

	private Nature findNature(String natureName) {
		NatureClientResponse apiNature = pokeApiClient.getNature(natureName);
		return new Nature(apiNature.name(), apiNature.increasedStat(), apiNature.decreasedStat());
	}
}