package net.majo24.mob_armor_trims.mixin.compatibility;

import io.github.flemmli97.improvedmobs.utils.Utils;
import net.majo24.mob_armor_trims.MobArmorTrims;
import net.minecraft.entity.mob.MobEntity;
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
        MobArmorTrims.randomlyApplyRandomTrims(living.getWorld().getRegistryManager(), living.getRandom(), living.getArmorItems());
    }
}
