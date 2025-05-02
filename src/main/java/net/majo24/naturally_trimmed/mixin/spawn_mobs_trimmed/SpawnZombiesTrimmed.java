package net.majo24.naturally_trimmed.mixin.spawn_mobs_trimmed;

import net.majo24.naturally_trimmed.TrimApplier;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class SpawnZombiesTrimmed extends LivingEntity {
    protected SpawnZombiesTrimmed(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    //? if >=1.21.2 {
    private void trimEquipment(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
    //?} else if 1.20.6 || 1.21 {
    /*private void trimEquipment(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
    *///?} else {
    /*private void trimEquipment(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, net.minecraft.nbt.CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> cir) {
    *///?}
        TrimApplier.trimEquipment(this);
    }
}
