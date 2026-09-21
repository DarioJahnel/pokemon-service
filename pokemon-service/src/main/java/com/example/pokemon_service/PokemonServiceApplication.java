package com.example.pokemon_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import reactor.netty.http.client.HttpClient;
import skaro.pokeapi.PokeApiReactorNonCachingConfiguration;

@SpringBootApplication
@Import(PokeApiReactorNonCachingConfiguration.class)
public class PokemonServiceApplication {

	@Bean
	public HttpClient httpClient() {
		return HttpClient.create();
	}

	public static void main(String[] args) {
		SpringApplication.run(PokemonServiceApplication.class, args);
	}

}
