package net.majo24.mob_armor_trims;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class TrimLootTablesFunction extends LootItemConditionalFunction {
    protected TrimLootTablesFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    public static final MapCodec<TrimLootTablesFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance)
                    .apply(instance, TrimLootTablesFunction::new)
    );

    @Override
    public @NotNull LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        //? if fabric {
        return MobArmorTrims.TRIM_LOOT_TABLES_FUNCTION;
        //?} else {
        /*return MobArmorTrims.TRIM_LOOT_TABLES_FUNCTION.value();
        *///?}
    }

    @Override
    protected @NotNull ItemStack run(ItemStack itemStack, LootContext lootContext) {
        System.out.println("Running TrimsLootTablesFunction");
        if (!itemStack.is(ItemTags.TRIMMABLE_ARMOR)) return itemStack;

        RandomSource random = lootContext.getRandom();
        RegistryAccess registryAccess = lootContext.getLevel().registryAccess();

        Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = TrimApplier.getTrimRegistries(registryAccess);
        Registry<TrimMaterial> materialRegistry = registries.getFirst();
        Registry<TrimPattern> patternRegistry = registries.getSecond();

        TrimApplier.applyRandomTrim(registryAccess, materialRegistry, patternRegistry, random, itemStack, null);
        return itemStack;
    }


    public static LootItemConditionalFunction.Builder<?> builder() {
        System.out.println("CREATED BUILDER!");
        return simpleBuilder(TrimLootTablesFunction::new);
    }
}
