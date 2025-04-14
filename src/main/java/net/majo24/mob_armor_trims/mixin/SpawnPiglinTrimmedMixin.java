package net.majo24.mob_armor_trims.mixin;

import net.majo24.mob_armor_trims.TrimApplier;import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Piglin.class)
public abstract class SpawnPiglinTrimmedMixin extends LivingEntity {
    protected SpawnPiglinTrimmedMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("TAIL"))
    private void trimArmor(RandomSource randomSource, DifficultyInstance difficultyInstance, CallbackInfo ci) {
        //? if >=1.21.5 {
        List<ItemStack> armor = EquipmentSlotGroup.ARMOR.slots().stream()
                .map(this::getItemBySlot)
                .filter(armorPiece -> !armorPiece.isEmpty())
                .toList();
        //?} else {
        /*List<ItemStack> armor = java.util.stream.StreamSupport.stream(this.getArmorSlots().spliterator(), false)
                .filter(armorPiece -> !armorPiece.isEmpty())
                .toList();
        *///?}

        if (!armor.isEmpty()) {
            TrimApplier.applyTrims(this.level().registryAccess(), super.random, armor, true);
        }
    }
}
