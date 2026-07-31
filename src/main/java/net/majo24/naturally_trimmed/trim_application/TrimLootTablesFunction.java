package net.majo24.naturally_trimmed.trim_application;

//? if >=1.21.11 {
import net.minecraft.core.component.DataComponents;
//?} else
//import net.minecraft.world.item.ArmorItem;

import net.majo24.naturally_trimmed.config.Config;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.NoSuchElementException;

import static net.majo24.naturally_trimmed.NaturallyTrimmed.LOGGER;


public class TrimLootTablesFunction extends LootItemConditionalFunction {
    protected TrimLootTablesFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    public static final MapCodec<TrimLootTablesFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance).apply(instance, TrimLootTablesFunction::new)
    );

    //? if >=26.1 {
    @Override
    public @NotNull MapCodec<TrimLootTablesFunction> codec() { return CODEC; }
    //?} else {
    /*@Override
    public @NotNull net.minecraft.world.level.storage.loot.functions.LootItemFunctionType getType() {
        //? if fabric {
        return net.majo24.naturally_trimmed.RegistryHelper.TRIM_LOOT_TABLES_FUNCTION;
        //?} else
        //return net.majo24.naturally_trimmed.RegistryHelper.TRIM_LOOT_TABLES_FUNCTION.get();
    }
    *///?}

    @Override
    protected @NotNull ItemStack run(ItemStack itemStack, @NotNull LootContext lootContext) {
        // === Check if a trim should be applied ===
        if (!Config.INSTANCE.enableTrimLootTables) return itemStack;
        if (!itemStack.is(ItemTags.TRIMMABLE_ARMOR) && !itemStack.is(ToolTrimsCompat.TRIMMABLE_TOOLS_TAG))
            return itemStack;

        RandomSource random = lootContext.getRandom();
        if (Config.INSTANCE.trimLootTables.trimChance < random.nextInt(100)) return itemStack;

        // === Apply a trim ===
        RegistryAccess registryAccess = lootContext.getLevel().registryAccess();

        if (itemStack.is(ItemTags.TRIMMABLE_ARMOR)) {
            //? if >=1.21.11 {
            if (itemStack.has(DataComponents.EQUIPPABLE)) {
            //?} else
            //if (itemStack.getItem() instanceof ArmorItem) {
                ArmorTrim trim;
                try {
                    trim = TrimApplier.getRandomTrim(registryAccess, random, List.of(itemStack));
                } catch (NoSuchElementException err) {
                    LOGGER.warn(err.getMessage());
                    return itemStack;
                }

                TrimApplier.applyTrim(itemStack, trim);
            } else {
                ToolTrimsCompat.applyTrimToTool(itemStack, registryAccess, random);
            }
        } else {
            ToolTrimsCompat.applyTrimToTool(itemStack, registryAccess, random);
        }
        return itemStack;
    }


    public static LootItemConditionalFunction.Builder<?> builder() {
        return simpleBuilder(TrimLootTablesFunction::new);
    }
}
