package com.example.pokemon_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.pokemon_service.client.PokeApiClientWrapper;
import com.example.pokemon_service.dto.CreatePokemonRequest;
import com.example.pokemon_service.dto.EvolvePokemonRequest;
import com.example.pokemon_service.dto.NatureClientResponse;
import com.example.pokemon_service.dto.PokemonClientResponse;
import com.example.pokemon_service.dto.PokemonDTO;
import com.example.pokemon_service.dto.Stat;
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

@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

	private static final Integer TEAM_CAPACITY = 6;
	private static final Integer PC_BOX_CAPACITY = 20;

	@Mock
	private PokemonRepository pokemonRepository;

	@Mock
	private TrainerRepository trainerRepository;

	@Mock
	private NatureRepository natureRepository;

	@Mock
	private PokeApiClientWrapper pokeApiClient;

	private PokemonService service;

	@BeforeEach
	void setUp() {
		service = new PokemonService(
				pokemonRepository,
				trainerRepository,
				natureRepository,
				pokeApiClient,
				TEAM_CAPACITY,
				PC_BOX_CAPACITY);
	}

	@Test
	void createPokemon_returnsMappedPokemon_whenTrainerHasSpace() {
		UUID trainerId = UUID.randomUUID();
		Trainer trainer = trainer(trainerId, 0, 0);
		when(trainerRepository.findByIdForUpdate(trainerId)).thenReturn(Optional.of(trainer));
		stubPikachuApi();
		when(pokemonRepository.findTrainerPokemonsByLocation(eq(trainerId), eq(PokemonLocation.TEAM),
				any(Pageable.class))).thenReturn(Page.empty());
		when(natureRepository.save(any(Nature.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(pokemonRepository.save(any(Pokemon.class))).thenAnswer(invocation -> invocation.getArgument(0));

		PokemonDTO result = service.createPokemon(createPokemonRequest(trainerId));

		assertEquals("pikachu", result.getSpecies());
		assertEquals(trainerId, result.getTrainerId());
		assertEquals("TEAM", result.getLocation());
		assertEquals(1, result.getTeamSlot());
		assertEquals("timid", result.getNature());
		verify(trainer).setTeamCount(1);
		assertEquals(99, result.getStats().stream().filter(stat -> stat.name().equals("SPEED")).findFirst()
				.orElseThrow().value());
		verify(pokemonRepository).save(any(Pokemon.class));
	}

	@Test
	void createPokemon_throws_whenTrainerHasNoSpaceLeft() {
		UUID trainerId = UUID.randomUUID();
		Trainer trainer = trainer(trainerId, TEAM_CAPACITY, PC_BOX_CAPACITY);
		when(trainerRepository.findByIdForUpdate(trainerId)).thenReturn(Optional.of(trainer));
		stubPikachuApi();

		assertThrows(RuntimeException.class, () -> service.createPokemon(createPokemonRequest(trainerId)));
	}

	@Test
	void createPokemon_movesPokemonToPcBox_whenTeamHasNoSpaceLeft() {
		UUID trainerId = UUID.randomUUID();
		Trainer trainer = trainer(trainerId, TEAM_CAPACITY, 0);
		when(trainerRepository.findByIdForUpdate(trainerId)).thenReturn(Optional.of(trainer));
		stubPikachuApi();
		when(natureRepository.save(any(Nature.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(pokemonRepository.save(any(Pokemon.class))).thenAnswer(invocation -> invocation.getArgument(0));

		PokemonDTO result = service.createPokemon(createPokemonRequest(trainerId));

		assertEquals("PC_BOX", result.getLocation());
		assertNull(result.getTeamSlot());
		verify(trainer).setPcBoxCount(1);
	}

	@Test
	void movePokemon_movesTeamPokemonToPcBox_whenSpaceAvailable() {
		UUID trainerId = UUID.randomUUID();
		UUID pokemonId = UUID.randomUUID();
		Trainer trainer = trainer(trainerId, 1, 0);

		Pokemon pokemon = pokemon(pokemonId, trainerId, "pikachu", Genre.MALE, PokemonLocation.TEAM, 2, "static",
				"timid");
		when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.of(pokemon));
		when(trainerRepository.findByIdForUpdate(trainerId)).thenReturn(Optional.of(trainer));
		when(pokemonRepository.save(any(Pokemon.class))).thenAnswer(invocation -> invocation.getArgument(0));

		PokemonDTO result = service.movePokemon(pokemonId);

		assertEquals("PC_BOX", result.getLocation());
		assertNull(result.getTeamSlot());
		verify(trainer).setTeamCount(0);
		verify(trainer).setPcBoxCount(1);
	}

	@Test
	void movePokemon_movesPcBoxPokemonToTeam_whenSpaceAvailable() {
		UUID trainerId = UUID.randomUUID();
		UUID pokemonId = UUID.randomUUID();
		Trainer trainer = trainer(trainerId, 0, 1);

		Pokemon pokemon = pokemon(pokemonId, trainerId, "pikachu", Genre.MALE, PokemonLocation.PC_BOX, null, "static",
				"timid");
		when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.of(pokemon));
		when(trainerRepository.findByIdForUpdate(trainerId)).thenReturn(Optional.of(trainer));
		when(pokemonRepository.findTrainerPokemonsByLocation(eq(trainerId), eq(PokemonLocation.TEAM),
				any(Pageable.class))).thenReturn(Page.empty());
		when(pokemonRepository.save(any(Pokemon.class))).thenAnswer(invocation -> invocation.getArgument(0));

		PokemonDTO result = service.movePokemon(pokemonId);

		assertEquals("TEAM", result.getLocation());
		assertEquals(1, result.getTeamSlot());
		verify(trainer).setPcBoxCount(0);
		verify(trainer).setTeamCount(1);
	}

	@Test
	void evolvePokemon_updatesSpeciesAndAbility_whenEvolutionIsValid() {
		UUID trainerId = UUID.randomUUID();
		UUID pokemonId = UUID.randomUUID();
		Pokemon pokemon = pokemon(pokemonId, trainerId, "pikachu", Genre.MALE, PokemonLocation.TEAM, 1, "static",
				"timid");
		when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.of(pokemon));
		when(pokeApiClient.getPokemonSpecies("raichu"))
				.thenReturn(new com.example.pokemon_service.dto.PokemonClientSpeciesResponse("pikachu"));
		when(pokeApiClient.getPokemon("raichu"))
				.thenReturn(apiPokemon("raichu", "static", "light-ball", "thunder-shock", "quick-attack"));

		PokemonDTO result = service.evolvePokemon(pokemonId, new EvolvePokemonRequest("raichu"));

		assertEquals("raichu", result.getSpecies());
		assertEquals("static", result.getAbility());
	}

	@Test
	void getPokemon_returnsMappedPokemon_whenPokemonExists() {
		UUID pokemonId = UUID.randomUUID();
		UUID trainerId = UUID.randomUUID();
		Trainer trainer = trainer(trainerId, 2, 0);
		Nature nature = new Nature("modest", StatType.SPECIAL_ATTACK, StatType.ATTACK);
		Pokemon pokemon = new Pokemon()
				.setId(pokemonId)
				.setSpecies("pikachu")
				.setTrainer(trainer)
				.setGenre(Genre.MALE)
				.setAbility("static")
				.setShiny(true)
				.setLocation(PokemonLocation.TEAM)
				.setTeamSlot(2)
				.setHeldItem("light-ball")
				.setMovements(Set.of("thunder-shock", "quick-attack"))
				.setCaptureLocation("viridian-city")
				.setNature(nature)
				.setStats(List.of(
						new PokemonStat(null, StatType.HP, 35, 0, 5),
						new PokemonStat(null, StatType.ATTACK, 55, 0, 10),
						new PokemonStat(null, StatType.DEFENSE, 40, 0, 10),
						new PokemonStat(null, StatType.SPECIAL_ATTACK, 90, 0, 24),
						new PokemonStat(null, StatType.SPECIAL_DEFENSE, 80, 0, 12),
						new PokemonStat(null, StatType.SPEED, 100, 0, 20)));

		when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.of(pokemon));

		PokemonDTO result = service.getPokemon(pokemonId);

		assertEquals("pikachu", result.getSpecies());
		assertEquals(trainerId, result.getTrainerId());
		assertEquals("TEAM", result.getLocation());
		assertEquals(2, result.getTeamSlot());
		assertEquals("modest", result.getNature());
		assertEquals(99, result.getStats().stream().filter(stat -> stat.name().equals("SPECIAL_ATTACK")).findFirst()
				.orElseThrow().value());
		assertEquals(100, result.getStats().stream().filter(stat -> stat.name().equals("SPEED")).findFirst()
				.orElseThrow().value());
	}

	private Pokemon pokemon(UUID id, UUID trainerId, String species, Genre genre, PokemonLocation location,
			Integer teamSlot, String ability, String natureName) {
		Trainer pokemonTrainer = trainer(trainerId);
		return new Pokemon()
				.setId(id)
				.setSpecies(species)
				.setTrainer(pokemonTrainer)
				.setGenre(genre)
				.setAbility(ability)
				.setLocation(location)
				.setTeamSlot(teamSlot)
				.setNature(new Nature(natureName, StatType.SPEED, StatType.ATTACK))
				.setStats(List.of(
						new PokemonStat(null, StatType.HP, 35, 0, 5),
						new PokemonStat(null, StatType.ATTACK, 55, 0, 10),
						new PokemonStat(null, StatType.DEFENSE, 40, 0, 10),
						new PokemonStat(null, StatType.SPECIAL_ATTACK, 90, 0, 24),
						new PokemonStat(null, StatType.SPECIAL_DEFENSE, 80, 0, 12),
						new PokemonStat(null, StatType.SPEED, 100, 0, 20)));
	}

	private PokemonClientResponse.Pokemon apiPokemon(String name, String abilityName, String heldItem,
			String... moves) {
		return new PokemonClientResponse.Pokemon(
				25,
				name,
				4,
				60,
				List.of(new PokemonClientResponse.AbilitySlot(false, 1,
						new PokemonClientResponse.NamedResource(abilityName,
								"https://example.com/ability/" + abilityName))),
				List.of(new PokemonClientResponse.HeldItem(
						new PokemonClientResponse.NamedResource(heldItem, "https://example.com/item/" + heldItem),
						List.of())),
				List.of(
						new PokemonClientResponse.MoveSlot(new PokemonClientResponse.NamedResource(moves[0],
								"https://example.com/move/" + moves[0]), List.of()),
						new PokemonClientResponse.MoveSlot(new PokemonClientResponse.NamedResource(moves[1],
								"https://example.com/move/" + moves[1]), List.of())),
				new PokemonClientResponse.NamedResource("pikachu", "https://example.com/species/pikachu"),
				List.of());
	}

	private CreatePokemonRequest createPokemonRequest(UUID trainerId) {
		return new CreatePokemonRequest(
				"pikachu",
				trainerId,
				"MALE",
				"static",
				true,
				"viridian-city",
				"light-ball",
				List.of(
						new Stat("HP", 45, 0, 31),
						new Stat("ATTACK", 55, 0, 20),
						new Stat("DEFENSE", 40, 0, 15),
						new Stat("SPECIAL_ATTACK", 50, 0, 10),
						new Stat("SPECIAL_DEFENSE", 50, 0, 12),
						new Stat("SPEED", 90, 0, 26)),
				Set.of("thunder-shock", "quick-attack"),
				"timid");
	}

	private Trainer trainer(UUID id) {
		Trainer trainer = mock(Trainer.class);
		lenient().when(trainer.getId()).thenReturn(id);
		return trainer;
	}

	private Trainer trainer(UUID id, int teamCount, int pcBoxCount) {
		Trainer trainer = trainer(id);
		lenient().when(trainer.getTeamCount()).thenReturn(teamCount);
		lenient().when(trainer.getPcBoxCount()).thenReturn(pcBoxCount);
		return trainer;
	}

	private void stubPikachuApi() {
		when(pokeApiClient.getNature("timid"))
				.thenReturn(new NatureClientResponse("timid", StatType.SPEED, StatType.ATTACK));
		when(pokeApiClient.getPokemon("pikachu"))
				.thenReturn(apiPokemon("pikachu", "static", "light-ball", "thunder-shock", "quick-attack"));
	}

}
