package net.majo24.mob_armor_trims;

import net.fabricmc.api.ModInitializer;

import net.majo24.mob_armor_trims.config.ConfigManager;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.util.math.random.Random;

public class MobArmorTrims implements ModInitializer {
	public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ConfigManager configManager = new ConfigManager(ConfigManager.getConfigFromFile());

	@Override
	public void onInitialize() {
	}

    public static void randomlyApplyRandomTrims(DynamicRegistryManager registryManager, Random random, Iterable<ItemStack> equippedArmor) {
        RegistryKey<Registry<ArmorTrimMaterial>> materialKey = RegistryKeys.TRIM_MATERIAL;
        Registry<ArmorTrimMaterial> materialRegistry = registryManager.get(materialKey);
        RegistryKey<Registry<ArmorTrimPattern>> patternKey = RegistryKeys.TRIM_PATTERN;
        Registry<ArmorTrimPattern> patternRegistry = registryManager.get(patternKey);

        DataComponentType<ArmorTrim> trimComponentType = DataComponentTypes.TRIM;

        for (ItemStack armor : equippedArmor) {
            if (MobArmorTrims.configManager.getTrimChance() < random.nextInt(100)) {continue;}
            if (armor.getItem() != Items.AIR) {
                applyRandomTrim(trimComponentType, registryManager, materialRegistry, patternRegistry, random, armor);
            }
        }
	}

    private static void applyRandomTrim(DataComponentType<ArmorTrim> trimComponentType, DynamicRegistryManager registryManager, Registry<ArmorTrimMaterial> materialRegistry, Registry<ArmorTrimPattern> patternRegistry, Random random, ItemStack armor) {
        RegistryEntry.Reference<ArmorTrimMaterial> randomTrimMaterial = materialRegistry.getRandom(random).get();
        RegistryEntry.Reference<ArmorTrimPattern> randomTrimPattern = patternRegistry.getRandom(random).get();
        ArmorTrim armorTrim = new ArmorTrim(randomTrimMaterial, randomTrimPattern);

        armor.applyChanges(ComponentChanges.builder().add(trimComponentType, armorTrim).build());
    }
}