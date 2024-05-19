package net.mob_armor_trims.majo24.mixin.spawn_entities_trimmed;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.mob_armor_trims.majo24.RandomTrims;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Mob.class)
public abstract class OnNaturalSpawnMixin extends LivingEntity {
    @Shadow public abstract @NotNull Iterable<ItemStack> getArmorSlots();

    @Inject(at = @At("TAIL"), method = "populateDefaultEquipmentSlots")
    private void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance, CallbackInfo ci) {
        
        RandomTrims.applyTrims(this.level().registryAccess(), super.random, this.getArmorSlots());
    }

    protected OnNaturalSpawnMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }
}