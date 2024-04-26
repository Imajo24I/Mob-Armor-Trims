package net.majo24.mob_armor_trims.mixin.spawn_entities_with_trims;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.majo24.mob_armor_trims.MobArmorTrims;

@Mixin(MobEntity.class)
public abstract class OnNaturalSpawnMixin extends LivingEntity {
    @Shadow public abstract Iterable<ItemStack> getArmorItems();

    protected OnNaturalSpawnMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "initEquipment")
    protected void initEquipment(CallbackInfo ci) {
        MobArmorTrims.randomlyApplyRandomTrims(this.getWorld().getRegistryManager(), this.random, this.getArmorItems());
    }
}