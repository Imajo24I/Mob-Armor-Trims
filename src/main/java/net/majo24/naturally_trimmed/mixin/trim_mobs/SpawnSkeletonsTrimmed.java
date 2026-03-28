package net.majo24.naturally_trimmed.mixin.trim_mobs;

import net.majo24.naturally_trimmed.trim_application.TrimApplier;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? >=1.21.11 {
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
//?} else
//import net.minecraft.world.entity.monster.AbstractSkeleton;

@Mixin(AbstractSkeleton.class)
public abstract class SpawnSkeletonsTrimmed extends LivingEntity {
    protected SpawnSkeletonsTrimmed(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    //? if >=1.21.2 {
    private void trimEquipment(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
    //?} else
    //private void trimEquipment(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        TrimApplier.trimEquipment(this);
    }
}
