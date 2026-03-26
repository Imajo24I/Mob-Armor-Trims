package net.majo24.naturally_trimmed.trim_application;

//? if 1.20.1 {
/*import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
*///?} else {
import com.mojang.serialization.MapCodec;
//?}

import net.majo24.naturally_trimmed.config.Config;
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


public class TrimLootTablesFunction extends LootItemConditionalFunction {
    protected TrimLootTablesFunction(/*? >1.20.1 {*/List<LootItemCondition>/*?} else {*//*LootItemCondition[]*//*?}*/ predicates) {
        super(predicates);
    }

    //? if >=1.21 {
    public static final MapCodec<TrimLootTablesFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance).apply(instance, TrimLootTablesFunction::new)
    );
    //?}

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
        if (!Config.CONFIG_MANAGER.instance().enableTrimLootTables) return itemStack;
        if (!itemStack.is(ItemTags.TRIMMABLE_ARMOR) && !itemStack.is(ToolTrimsCompat.TRIMMABLE_TOOLS_TAG))
            return itemStack;

        RandomSource random = lootContext.getRandom();
        if (Config.CONFIG_MANAGER.instance().trimLootTables.trimChance < random.nextInt(100)) return itemStack;

        // === Apply a trim ===
        RegistryAccess registryAccess = lootContext.getLevel().registryAccess();
        ArmorTrim trim = TrimApplier.getRandomTrim(registryAccess, random);
        if (trim == null) return itemStack;

        if (itemStack.is(ItemTags.TRIMMABLE_ARMOR)) {
            TrimApplier.applyTrim(itemStack, trim, registryAccess);
        } else {
            ToolTrimsCompat.applyTrimToTool(itemStack, registryAccess, random);
        }
        return itemStack;
    }


    public static LootItemConditionalFunction.Builder<?> builder() {
        return simpleBuilder(TrimLootTablesFunction::new);
    }

    //? if 1.20.1 {
    /*public static class Serializer extends LootItemConditionalFunction.Serializer<TrimLootTablesFunction> {
        @Override
        public @NotNull TrimLootTablesFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            return new TrimLootTablesFunction(conditions);
        }
    }
    *///?}
}
