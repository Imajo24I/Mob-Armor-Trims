package net.majo24.mob_armor_trims.config;

import net.majo24.mob_armor_trims.config.custom_trim_combinations.TrimCombination;

import java.util.List;

public class Config {
    // General
    private TrimSystems enabledSystem;

    // Random Trims System
    private int trimChance;
    private int similarTrimChance;
    private int noTrimsChance;

    // System of giving mobs only armor out of custom trims list
    private List<TrimCombination> trimCombinations;

    // Stacked Armor Trims Compatiblity
    private int stackedTrimChance;
    private int maxStackedTrims;

    public Config(TrimSystems enabledSystem, int trimChance, int similarTrimChance, int noTrimsChance, List<TrimCombination> trimCombinations, int stackedTrimChance, int maxStackedTrims) {
        this.enabledSystem = enabledSystem;

        this.trimChance = trimChance;
        this.similarTrimChance = similarTrimChance;
        this.noTrimsChance = noTrimsChance;

        this.trimCombinations = trimCombinations;

        this.stackedTrimChance = stackedTrimChance;
        this.maxStackedTrims = maxStackedTrims;
    }

    public TrimSystems getEnabledSystem() {
        return enabledSystem;
    }

    public void setEnabledSystem(TrimSystems enabledSystem) {
        this.enabledSystem = enabledSystem;
    }

    public int getTrimChance() {
        return trimChance;
    }

    public void setTrimChance(int trimChance) {
        this.trimChance = trimChance;
    }

    public int getSimilarTrimChance() {
        return similarTrimChance;
    }

    public void setSimilarTrimChance(int similarTrimChance) {
        this.similarTrimChance = similarTrimChance;
    }

    public int getNoTrimsChance() {
        return noTrimsChance;
    }

    public void setNoTrimsChance(int noTrimsChance) {
        this.noTrimsChance = noTrimsChance;
    }

    public List<TrimCombination> getTrimCombinations() {
        return trimCombinations;
    }

    public void setTrimCombinations(List<TrimCombination> trimCombinations) {
        this.trimCombinations = trimCombinations;
    }

    public int getStackedTrimChance() {
        return stackedTrimChance;
    }

    public void setStackedTrimChance(int stackedTrimChance) {
        this.stackedTrimChance = stackedTrimChance;
    }

    public int getMaxStackedTrims() {
        return maxStackedTrims;
    }

    public void setMaxStackedTrims(int maxStackedTrims) {
        this.maxStackedTrims = maxStackedTrims;
    }

    public enum TrimSystems {
        RANDOM_TRIMS,
        CUSTOM_TRIM_COMBINATIONS,
        NONE,
    }
}