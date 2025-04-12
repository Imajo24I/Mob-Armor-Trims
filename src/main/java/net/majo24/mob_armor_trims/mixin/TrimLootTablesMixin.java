package net.majo24.mob_armor_trims.mixin;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import net.majo24.mob_armor_trims.TrimLootTablesFunction;
import net.minecraft.core.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ReloadableServerRegistries.class)
public abstract class TrimLootTablesMixin {
    private TrimLootTablesMixin() {
    }

    //? if >=1.21.5 {
    @Inject(
            method = "method_61240",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V")
    )

    private static <T> void modifyLootTable(LootDataType<T> lootDataType, ResourceManager resourceManager, RegistryOps<JsonElement> registryOps, CallbackInfoReturnable<WritableRegistry<?>> cir, @Local Map<ResourceLocation, T> map) {
        map.forEach((identifier, t) -> modifyLootTable(t));
    }
    //?} else {
    /*@WrapOperation(method = "method_58278", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private static <T> void modifyTables(Optional<T> optionalTable, Consumer<? super T> action, Operation<Void> original) {
        original.call(optionalTable.map(TrimLootTablesMixin::modifyLootTable), action);
    }
    *///?}


    @Unique
    private static <T> T modifyLootTable(T value) {
        if (!(value instanceof LootTable table) || table == LootTable.EMPTY) return value;

        table.functions = ImmutableList.<LootItemFunction>builder()
                .addAll(table.functions)
                .add(TrimLootTablesFunction.builder().build())
                .build();

        return (T) table;
    }
}
