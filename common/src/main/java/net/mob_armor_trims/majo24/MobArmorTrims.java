package net.mob_armor_trims.majo24;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.mob_armor_trims.majo24.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class MobArmorTrims {
    public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean isStackedArmorTrimsLoaded = false;
    public static ConfigManager configManager;

    public static void init(Path configPath) {
        configManager = new ConfigManager(ConfigManager.getConfigFromFile(configPath), configPath);
    }

    public static void randomlyApplyRandomTrims(RegistryAccess registryAccess, RandomSource random, Iterable<ItemStack> equippedArmor) {
        ResourceKey<Registry<TrimMaterial>> materialKey = Registries.TRIM_MATERIAL;
        Registry<TrimMaterial> materialRegistry = registryAccess.registryOrThrow(materialKey);
        ResourceKey<Registry<TrimPattern>> patternKey = Registries.TRIM_PATTERN;
        Registry<TrimPattern> patternRegistry = registryAccess.registryOrThrow(patternKey);

        for (ItemStack armor : equippedArmor) {
            if (configManager.getTrimChance() < random.nextInt(100)) {continue;}
            if (armor.getItem() != Items.AIR) {
                applyRandomTrim(registryAccess, materialRegistry, patternRegistry, random, armor);
            }

            // TODO: add code for compat with stacked armor trims
        }
    }

    public static void applyRandomTrim(RegistryAccess registryAccess, Registry<TrimMaterial> materialRegistry, Registry<TrimPattern> patternRegistry, RandomSource random, ItemStack armor) {
        Holder.Reference<TrimMaterial> randomTrimMaterial = materialRegistry.getRandom(random).get();
        Holder.Reference<TrimPattern> randomTrimPattern = patternRegistry.getRandom(random).get();

        ArmorTrim armorTrim = new ArmorTrim(randomTrimMaterial, randomTrimPattern);
        ArmorTrim.setTrim(registryAccess, armor, armorTrim);
    }
}
