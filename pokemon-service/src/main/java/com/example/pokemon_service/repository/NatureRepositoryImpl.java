package com.example.pokemon_service.repository;

import org.springframework.stereotype.Repository;

import com.example.pokemon_service.model.Nature;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class NatureRepositoryImpl implements NatureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Nature save(Nature nature) {
        Nature existing = entityManager.find(Nature.class, nature.getName());
        if (existing != null) {
            return existing;
        }

        entityManager.persist(nature);
        return nature;
    }
}
