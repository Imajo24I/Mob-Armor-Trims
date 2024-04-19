package net.majo24.mob_armor_trims.mixin.compatibility;

import io.github.flemmli97.improvedmobs.utils.Utils;
import net.majo24.mob_armor_trims.MobArmorTrims;
import net.minecraft.entity.mob.MobEntity;
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
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Utils.class)
public class ImprovedMobsCompatibilityMixin {
    private ImprovedMobsCompatibilityMixin() {
    }
    @Inject(at = @At("TAIL"), method = "equipArmor")
    private static void equipArmor(MobEntity living, float difficulty, CallbackInfo ci) {
        DynamicRegistryManager registryManager = living.getWorld().getRegistryManager();
        RegistryKey<Registry<ArmorTrimMaterial>> materialKey = RegistryKeys.TRIM_MATERIAL;
        Registry<ArmorTrimMaterial> materialRegistry = registryManager.get(materialKey);
        RegistryKey<Registry<ArmorTrimPattern>> patternKey = RegistryKeys.TRIM_PATTERN;
        Registry<ArmorTrimPattern> patternRegistry = registryManager.get(patternKey);living.getWorld().getRegistryManager();

        Random random = living.getRandom();

        Iterable<ItemStack> equippedArmor = living.getArmorItems();

        for (ItemStack armor : equippedArmor) {
            if (MobArmorTrims.configManager.getTrimChance() < random.nextInt(100)) {continue;}
            if (armor.getItem() != Items.AIR) {
                RegistryEntry.Reference<ArmorTrimMaterial> randomTrimMaterial = materialRegistry.getRandom(random).get();
                RegistryEntry.Reference<ArmorTrimPattern> randomTrimPattern = patternRegistry.getRandom(random).get();

                ArmorTrim armorTrim = new ArmorTrim(randomTrimMaterial, randomTrimPattern);
                ArmorTrim.apply(registryManager, armor, armorTrim);
            }
        }

    }
}
