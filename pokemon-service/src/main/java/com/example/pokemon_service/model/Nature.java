package com.example.pokemon_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class Nature {
    @Id
    private String name;

    @Enumerated(EnumType.STRING)
    private StatType increasedStat;

    @Enumerated(EnumType.STRING)
    private StatType decreasedStat;

    protected Nature() {
    }

    public Nature(String name, StatType increasedStat, StatType decreasedStat) {
        this.name = name;
        this.increasedStat = increasedStat;
        this.decreasedStat = decreasedStat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StatType getIncreasedStat() {
        return increasedStat;
    }

    public void setIncreasedStat(StatType increasedStat) {
        this.increasedStat = increasedStat;
    }

    public StatType getDecreasedStat() {
        return decreasedStat;
    }

    public void setDecreasedStat(StatType decreasedStat) {
        this.decreasedStat = decreasedStat;
    }

}
