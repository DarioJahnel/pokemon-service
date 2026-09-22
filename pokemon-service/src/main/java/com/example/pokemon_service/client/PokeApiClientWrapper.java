package com.example.pokemon_service.client;

import java.util.Locale;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.pokemon_service.dto.NatureClientResponse;
import com.example.pokemon_service.dto.PokemonClientResponse;
import com.example.pokemon_service.dto.PokemonClientSpeciesResponse;
import com.example.pokemon_service.exception.PokeApiClientException;
import com.example.pokemon_service.model.StatType;

import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.nature.Nature;
import skaro.pokeapi.resource.pokemon.Pokemon;
import skaro.pokeapi.resource.pokemon.PokemonAbility;
import skaro.pokeapi.resource.pokemon.PokemonHeldItem;
import skaro.pokeapi.resource.pokemon.PokemonHeldItemVersion;
import skaro.pokeapi.resource.pokemon.PokemonMove;
import skaro.pokeapi.resource.pokemon.PokemonMoveVersion;
import skaro.pokeapi.resource.pokemon.PokemonType;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;

@Component
public class PokeApiClientWrapper {

	private final PokeApiClient client;

	public PokeApiClientWrapper(PokeApiClient client) {
		this.client = client;
	}

	public PokemonClientResponse.Pokemon getPokemon(String idOrName) {
		try {
			Pokemon pokemon = client.getResource(Pokemon.class, idOrName).block();
			return toPokemonClientResponse(pokemon);
		} catch (WebClientResponseException.NotFound e) {
			throw new PokeApiClientException("Couldn't find Pokemon " + idOrName);
		} catch (WebClientResponseException e) {
			throw new PokeApiClientException(
					"PokeAPI returned HTTP " + e.getStatusCode().value());
		} catch (WebClientRequestException e) {
			throw new PokeApiClientException("PokeAPI is unavailable");
		}
	}

	public PokemonClientSpeciesResponse getPokemonSpecies(String idOrName) {
		try {
			PokemonSpecies pokemonSpecies = client.getResource(PokemonSpecies.class, idOrName).block();
			String evolvesFrom = "";
			if (pokemonSpecies.getEvolvesFromSpecies() != null) {
				evolvesFrom = pokemonSpecies.getEvolvesFromSpecies().getName();
			}
			return new PokemonClientSpeciesResponse(evolvesFrom);
		} catch (WebClientResponseException.NotFound e) {
			throw new PokeApiClientException("Couldn't find PokemonSpecies " + idOrName);
		} catch (WebClientResponseException e) {
			throw new PokeApiClientException(
					"PokeAPI returned HTTP " + e.getStatusCode().value());
		} catch (WebClientRequestException e) {
			throw new PokeApiClientException("PokeAPI is unavailable");
		}
	}

	public NatureClientResponse getNature(String idOrName) {
		try {
			Nature nature = client.getResource(Nature.class, idOrName).block();
			return toNatureClientResponse(nature);
		} catch (WebClientResponseException.NotFound e) {
			throw new PokeApiClientException("Couldn't find Nature " + idOrName);
		} catch (WebClientResponseException e) {
			throw new PokeApiClientException(
					"PokeAPI returned HTTP " + e.getStatusCode().value());
		} catch (WebClientRequestException e) {
			throw new PokeApiClientException("PokeAPI is unavailable");
		}
	}

	private PokemonClientResponse.Pokemon toPokemonClientResponse(Pokemon pokemon) {
		return new PokemonClientResponse.Pokemon(
				pokemon.getId(),
				pokemon.getName(),
				pokemon.getHeight(),
				pokemon.getWeight(),
				pokemon.getAbilities().stream().map(this::toAbilitySlot).toList(),
				pokemon.getHeldItems().stream().map(this::toHeldItem).toList(),
				pokemon.getMoves().stream().map(this::toMoveSlot).toList(),
				toNamedResource(pokemon.getSpecies()),
				pokemon.getTypes().stream().map(this::toTypeSlot).toList());
	}

	private PokemonClientResponse.AbilitySlot toAbilitySlot(PokemonAbility ability) {
		return new PokemonClientResponse.AbilitySlot(ability.getIsHidden(), ability.getSlot(),
				toNamedResource(ability.getAbility()));
	}

	private PokemonClientResponse.HeldItem toHeldItem(PokemonHeldItem heldItem) {
		return new PokemonClientResponse.HeldItem(toNamedResource(heldItem.getItem()),
				heldItem.getVersionDetails().stream().map(this::toVersionDetail).toList());
	}

	private PokemonClientResponse.VersionDetail toVersionDetail(PokemonHeldItemVersion versionDetail) {
		return new PokemonClientResponse.VersionDetail(versionDetail.getRarity(),
				toNamedResource(versionDetail.getVersion()));
	}

	private PokemonClientResponse.MoveSlot toMoveSlot(PokemonMove move) {
		return new PokemonClientResponse.MoveSlot(toNamedResource(move.getMove()),
				move.getVersionGroupDetails().stream().map(this::toVersionGroupDetail).toList());
	}

	private PokemonClientResponse.VersionGroupDetail toVersionGroupDetail(PokemonMoveVersion versionDetail) {
		return new PokemonClientResponse.VersionGroupDetail(versionDetail.getLevelLearnedAt(),
				toNamedResource(versionDetail.getVersionGroup()), toNamedResource(versionDetail.getMoveLearnMethod()),
				null);
	}

	private PokemonClientResponse.TypeSlot toTypeSlot(PokemonType type) {
		return new PokemonClientResponse.TypeSlot(type.getSlot(), toNamedResource(type.getType()));
	}

	private PokemonClientResponse.NamedResource toNamedResource(NamedApiResource<?> resource) {
		return resource == null ? null : new PokemonClientResponse.NamedResource(resource.getName(), resource.getUrl());
	}

	private NatureClientResponse toNatureClientResponse(Nature nature) {
		return new NatureClientResponse(nature.getName(), toStatType(nature.getIncreasedStat()),
				toStatType(nature.getDecreasedStat()));
	}

	private StatType toStatType(NamedApiResource<?> stat) {
		if (stat == null || stat.getName() == null) {
			return null;
		}
		return StatType.valueOf(stat.getName().toUpperCase(Locale.ROOT).replace('-', '_'));
	}
}