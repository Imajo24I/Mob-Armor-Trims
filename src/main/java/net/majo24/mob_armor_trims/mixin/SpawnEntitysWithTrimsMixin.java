package net.majo24.mob_armor_trims.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.majo24.mob_armor_trims.MobArmorTrims;

@Mixin(MobEntity.class)
public abstract class SpawnEntitysWithTrimsMixin extends LivingEntity {
    protected SpawnEntitysWithTrimsMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Inject(at = @At("TAIL"), method = "initEquipment")
    protected void initEquipment(CallbackInfo ci) {
        DynamicRegistryManager registryManager = this.getWorld().getRegistryManager();
        RegistryKey<Registry<ArmorTrimMaterial>> materialKey = RegistryKeys.TRIM_MATERIAL;
        Registry<ArmorTrimMaterial> materialRegistry = registryManager.get(materialKey);
        RegistryKey<Registry<ArmorTrimPattern>> patternKey = RegistryKeys.TRIM_PATTERN;
        Registry<ArmorTrimPattern> patternRegistry = registryManager.get(patternKey);

        Iterable<ItemStack> equippedArmor = this.getArmorItems();

        for (ItemStack armor : equippedArmor) {
            if (MobArmorTrims.configManager.getTrimChance() < super.random.nextInt(100)) {continue;}
            if (armor.getItem() != Items.AIR) {
                RegistryEntry.Reference<ArmorTrimMaterial> randomTrimMaterial = materialRegistry.getRandom(super.random).get();
                RegistryEntry.Reference<ArmorTrimPattern> randomTrimPattern = patternRegistry.getRandom(super.random).get();

                ArmorTrim armorTrim = new ArmorTrim(randomTrimMaterial, randomTrimPattern);
                ArmorTrim.apply(registryManager, armor, armorTrim);
            }
        }
    }
}