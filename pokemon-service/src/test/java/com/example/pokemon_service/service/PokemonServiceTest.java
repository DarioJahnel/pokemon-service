package com.example.pokemon_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.example.pokemon_service.client.PokeApiClientWrapper;
import com.example.pokemon_service.dto.PokemonClientResponse;
import com.example.pokemon_service.repository.PokemonRepository;
import com.example.pokemon_service.repository.TrainerRepository;

import reactor.core.publisher.Mono;
import skaro.pokeapi.resource.pokemon.Pokemon;

class PokemonServiceTest {

	@Test
	void getPokemonDelegatesToPokeApiClient() {
/* 		PokeApiClientWrapper client = mock(PokeApiClientWrapper.class);
		Pokemon apiPokemon = new Pokemon();
		apiPokemon.setId(25);
		apiPokemon.setName("pikachu");
		apiPokemon.setBaseExperience(112);
		apiPokemon.setHeight(4);
		apiPokemon.setWeight(60);
		Mono<Pokemon> expected = Mono.just(apiPokemon);
		when(client.getResource(Pokemon.class, "pikachu")).thenReturn(expected);

		PokemonService service = new PokemonService(mock(PokemonRepository.class), mock(TrainerRepository.class),
				client, 6, 30);

		PokemonClientResponse actual = service.getPokemon("pikachu").block();
		assertEquals(new PokemonClientResponse(25, "pikachu", 112, 4, 60), actual);
		verify(client).getResource(Pokemon.class, "pikachu"); */
	}
}