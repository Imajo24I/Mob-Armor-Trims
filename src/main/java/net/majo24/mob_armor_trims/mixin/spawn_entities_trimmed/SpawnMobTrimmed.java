package net.majo24.mob_armor_trims.mixin.spawn_entities_trimmed;

import net.majo24.mob_armor_trims.TrimApplier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Mob.class)
public abstract class SpawnMobTrimmed extends LivingEntity {
    protected SpawnMobTrimmed(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "populateDefaultEquipmentSlots")
    private void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance, CallbackInfo ci) {
        TrimApplier.applyTrims(this.level().registryAccess(), super.random, this.getArmorSlots());
    }
}