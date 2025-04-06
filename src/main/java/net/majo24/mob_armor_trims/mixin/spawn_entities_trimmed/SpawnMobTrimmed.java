package net.majo24.mob_armor_trims.mixin.spawn_entities_trimmed;

import net.majo24.mob_armor_trims.TrimApplier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.ArrayList;
import java.util.List;

@Mixin(Mob.class)
public abstract class SpawnMobTrimmed extends LivingEntity {
    @Shadow public abstract void equip(EquipmentTable arg);

    protected SpawnMobTrimmed(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "populateDefaultEquipmentSlots")
    private void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance, CallbackInfo ci) {
        List<ItemStack> armor;

        //? if >=1.21.5 {
        armor = new ArrayList<>();

        for (EquipmentSlot equipmentSlot : EquipmentSlotGroup.ARMOR) {
            armor.add(this.getItemBySlot(equipmentSlot));
        }
        //?} else {
        /*armor = this.getArmorSlots();
        *///?}

        TrimApplier.applyTrims(this.level().registryAccess(), super.random, armor);
    }
}