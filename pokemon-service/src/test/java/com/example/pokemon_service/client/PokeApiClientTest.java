package com.example.pokemon_service.client;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import skaro.pokeapi.resource.pokemon.Pokemon;

class PokeApiClientTest {

    @Test
    void getResourceDelegatesToPokeApiReactor() {
        skaro.pokeapi.client.PokeApiClient delegate = mock(skaro.pokeapi.client.PokeApiClient.class);
        Mono<Pokemon> expected = Mono.empty();
        when(delegate.getResource(Pokemon.class, "pikachu")).thenReturn(expected);

        PokeApiClientWrapper client = new PokeApiClientWrapper(delegate);

        assertSame(expected, client.getPokemon("pikachu"));
        verify(delegate).getResource(Pokemon.class, "pikachu");
    }
}