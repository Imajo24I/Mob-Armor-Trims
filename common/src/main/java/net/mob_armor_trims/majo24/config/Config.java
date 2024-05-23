package net.mob_armor_trims.majo24.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.*;
import net.mob_armor_trims.majo24.MobArmorTrims;

import java.util.List;

public class Config {
    // General
    private TrimSystem enabledSystem;

    // Random Trims System
    private int trimChance;
    private int similarTrimChance;
    private int noTrimsChance;

    // System of giving mobs only armor out of custom trims list
    private List<CustomTrim> customTrimsList;

    // Stacked Armor Trims Compatiblity
    private int stackedTrimChance;
    private int maxStackedTrims;

    public Config(TrimSystem enabledSystem, int trimChance, int similarTrimChance, int noTrimsChance, List<CustomTrim> customTrimsList, int stackedTrimChance, int maxStackedTrims) {
        this.enabledSystem = enabledSystem;

        this.trimChance = trimChance;
        this.similarTrimChance = similarTrimChance;
        this.noTrimsChance = noTrimsChance;

        this.customTrimsList = customTrimsList;

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

    public List<CustomTrim> getCustomTrimsList() {
        return customTrimsList;
    }

    public void setCustomTrimsList(List<CustomTrim> customTrimsList) {
        this.customTrimsList = customTrimsList;
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

    public record CustomTrim(String material, String pattern) {
        public ArmorTrim getTrim(RegistryAccess registryAccess) {
            String trimPatternIdentifier = pattern;
            if (!trimPatternIdentifier.contains("_armor_trim_smithing_template")) {
                trimPatternIdentifier += "_armor_trim_smithing_template";
            }

            Holder.Reference<TrimMaterial> trimMaterial;
            Holder.Reference<TrimPattern> trimPattern;

            try {
                trimMaterial = TrimMaterials.getFromIngredient(registryAccess, BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(material)).getDefaultInstance()).orElseThrow();
                trimPattern = TrimPatterns.getFromTemplate(registryAccess, BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(trimPatternIdentifier)).getDefaultInstance()).orElseThrow();
            } catch (Exception e) {
                MobArmorTrims.LOGGER.error("Failed to apply custom trim. Please ensure this is a valid custom trim: {}; {} - {}", material, pattern, e);
                return null;
            }

            return new ArmorTrim(trimMaterial, trimPattern);
        }
    }

    public enum TrimSystem {
        RANDOM_TRIMS,
        CUSTOM_TRIMS,
        NONE,
    }
}