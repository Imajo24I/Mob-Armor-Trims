package net.majo24.naturally_trimmed.config.screen;

import net.majo24.naturally_trimmed.trim_combination.TrimCombination;
import net.majo24.naturally_trimmed.trim_combination.TrimKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.equipment.trim.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.majo24.naturally_trimmed.config.Config.CONFIG_MANAGER;

public class SettingsValidation {
    private SettingsValidation() {
    }

    public static void validateTrimCombinations() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || player == null) return;

        RegistryAccess registryAccess = level.registryAccess();

        player.displayClientMessage(Component.literal("Validating custom trim combinations...\n"), false);

        int index = 0;
        for (TrimCombination trimCombination : CONFIG_MANAGER.instance().trimMobs.trimCombinations) {
            validateTrimCombination(trimCombination, registryAccess, player, index);
            index++;
        }

        player.displayClientMessage(Component.literal("\nDone validating custom trim combinations"), false);
    }

    private static void validateTrimCombination(TrimCombination trimCombination, RegistryAccess registryAccess, LocalPlayer player, int index) {
        for (TrimKey trim : trimCombination.trims()) {
            if (trim.getTrim(registryAccess) == null) {
                player.displayClientMessage(Component.literal(
                        "Found invalid trim: \"" + trim + "\" in trim combination " + index
                ), false);
            }
        }
    }

    public static void validateBlacklist() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || player == null) return;

        //? >=1.21.2 {
        Registry<TrimPattern> patternRegistry = level.registryAccess().lookupOrThrow(Registries.TRIM_PATTERN);
        List<Holder.Reference<TrimPattern>> trimPatternsList = new ArrayList<>(patternRegistry.listElements().toList());
        //?} else {
        /*Registry<TrimPattern> patternRegistry = level.registryAccess().registryOrThrow(Registries.TRIM_PATTERN);
        List<Holder.Reference<TrimPattern>> trimPatternsList = new ArrayList<>(patternRegistry.holders().toList());
        *///?}

        Map<String, Boolean> trimPatterns = trimPatternsList.stream().collect(
                Collectors.toMap(trimPattern -> trimPattern.key().location().toString(), trimPattern -> false)
        );

        Map<Pattern, Boolean> blacklist = CONFIG_MANAGER.instance().blacklist.stream().collect(
                Collectors.toMap(pattern -> pattern, pattern -> false)
        );


        player.displayClientMessage(Component.literal("Validating blacklist...\n"), false);
        player.displayClientMessage(Component.literal("Checking for blacklisted trim patterns\n"), false);

        for (Map.Entry<Pattern, Boolean> pattern : blacklist.entrySet()) {
            for (Map.Entry<String, Boolean> trimPattern : trimPatterns.entrySet()) {
                if (pattern.getKey().matcher(trimPattern.getKey()).find()) {
                    pattern.setValue(true);
                    trimPattern.setValue(true);
                    player.displayClientMessage(Component.literal("Regex pattern \"" + pattern.getKey() + "\" blacklists trim pattern \"" + trimPattern.getKey() + "\""), false);
                }
            }
        }

        player.displayClientMessage(Component.literal("\nChecking for not blacklisted trim patterns\n"), false);

        for (Map.Entry<String, Boolean> trimPattern : trimPatterns.entrySet()) {
            if (!trimPattern.getValue()) {
                player.displayClientMessage(Component.literal("Trim pattern \"" + trimPattern.getKey() + "\" is not blacklisted"), false);
            }
        }

        player.displayClientMessage(Component.literal("\nChecking for unnecessary regex patterns\n"), false);

        for (Map.Entry<Pattern, Boolean> pattern : blacklist.entrySet()) {
            if (!pattern.getValue()) {
                player.displayClientMessage(Component.literal("Regex pattern \"" + pattern.getKey() + "\" does not blacklist any registered trim pattern."), false);
            }
        }

        player.displayClientMessage(Component.literal("\nDone validating blacklist"), false);
    }
}
