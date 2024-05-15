package net.mob_armor_trims.majo24.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
    // General
    private TrimSystem enabledSystem;

    // Random Trims System
    private int trimChance;
    private int similarTrimChance;
    private int noTrimsChance;

    // System of giving mobs only armor out of custom trims list
    private List<ArrayList<String>> customTrimsList;
    private String selectedMaterial;
    private String selectedPattern;

    // Stacked Armor Trims Compatiblity
    private int stackedTrimChance;
    private int maxStackedTrims;

    public Config(TrimSystem enabledSystem, int trimChance, int similarTrimChance, int noTrimsChance, List<ArrayList<String>> customTrimsList, String selectedMaterial, String selectedPattern, int stackedTrimChance, int maxStackedTrims) {
        this.enabledSystem = enabledSystem;

        this.trimChance = trimChance;
        this.similarTrimChance = similarTrimChance;
        this.noTrimsChance = noTrimsChance;

        this.customTrimsList = customTrimsList;
        this.selectedMaterial = selectedMaterial;
        this.selectedPattern = selectedPattern;

        this.stackedTrimChance = stackedTrimChance;
        this.maxStackedTrims = maxStackedTrims;
    }

    public TrimSystem getEnabledSystem() {
        return enabledSystem;
    }

    public void setEnabledSystem(TrimSystem enabledSystem) {
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

    public List<ArrayList<String>> getCustomTrimsList() {
        return customTrimsList;
    }

    public void setCustomTrimsList(List<ArrayList<String>> customTrimsList) {
        this.customTrimsList = customTrimsList;
    }

    public String getSelectedMaterial() {
        return selectedMaterial;
    }

    public void setSelectedMaterial(String selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
    }

    public String getSelectedPattern() {
        return selectedPattern;
    }

    public void setSelectedPattern(String selectedPattern) {
        this.selectedPattern = selectedPattern;
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
}