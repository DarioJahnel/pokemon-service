package com.example.pokemon_service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.netty.resolver.DefaultAddressResolverGroup;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import skaro.pokeapi.PokeApiReactorCachingConfiguration;

@SpringBootApplication
@Import(PokeApiReactorCachingConfiguration.class)
@EnableCaching
public class PokemonServiceApplication {

	@Bean
	public ConnectionProvider connectionProvider(
			@Value("${skaro.pokeapi.max.idle.time}") int maxIdleTime,
			@Value("${skaro.pokeapi.max.connections}") int maxConnections,
			@Value("${skaro.pokeapi.pending.acquire.connection}") int pendingAcquireMaxCount) {
		return ConnectionProvider.builder("Auto refresh & no connection limit")
				.maxIdleTime(Duration.ofSeconds(maxIdleTime))
				.maxConnections(maxConnections)
				.pendingAcquireMaxCount(pendingAcquireMaxCount)
				.build();
	}

	@Bean
	public HttpClient httpClient(ConnectionProvider connectionProvider) {
		return HttpClient.create(connectionProvider)
				.compress(true)
				.resolver(DefaultAddressResolverGroup.INSTANCE);
	}

	public static void main(String[] args) {
		SpringApplication.run(PokemonServiceApplication.class, args);
	}

}
