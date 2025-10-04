package net.majo24.naturally_trimmed.trim_application;

//? if 1.20.4 {
/*import com.mojang.serialization.Codec;
*///?} else if >1.20.4 {
import com.mojang.serialization.MapCodec;
//?} else {
/*import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
*///?}

import net.majo24.naturally_trimmed.RegistryHelper;
import net.majo24.naturally_trimmed.config.Config;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class TrimLootTablesFunction extends LootItemConditionalFunction {
    protected TrimLootTablesFunction(
            //? if >1.20.1 {
            List<LootItemCondition>
                    //?} else {
                    /*LootItemCondition[]
                     *///?}
                    predicates) {
        super(predicates);
    }

    //? if >1.20.1 {
    //? if 1.20.4 {
    /*public static final Codec<TrimLootTablesFunction> CODEC = RecordCodecBuilder.create(
    *///?} else {
    public static final MapCodec<TrimLootTablesFunction> CODEC = RecordCodecBuilder.mapCodec(
    //?}
            instance -> commonFields(instance)
                    .apply(instance, TrimLootTablesFunction::new)
    );
    //?}

    @Override
    public @NotNull LootItemFunctionType getType() {
        //? if fabric {
        return RegistryHelper.TRIM_LOOT_TABLES_FUNCTION;
        //?} else {
        /*return RegistryHelper.TRIM_LOOT_TABLES_FUNCTION.get();
         *///?}
    }

    @Override
    protected @NotNull ItemStack run(ItemStack itemStack, @NotNull LootContext lootContext) {
        if (!Config.CONFIG_MANAGER.instance().enableTrimLootTables) return itemStack;
        if (!itemStack.is(ItemTags.TRIMMABLE_ARMOR) && !itemStack.is(ToolTrimsCompat.TRIMMABLE_TOOL_TAG)) return itemStack;

        RandomSource random = lootContext.getRandom();
        RegistryAccess registryAccess = lootContext.getLevel().registryAccess();

        if (Config.CONFIG_MANAGER.instance().trimLootTables.trimChance < random.nextInt(100)) return itemStack;

        ArmorTrim trim = TrimApplier.getRandomTrim(registryAccess, random);
        if (trim == null) return itemStack;

        if (itemStack.is(ItemTags.TRIMMABLE_ARMOR)) {
            TrimApplier.applyTrim(itemStack, trim, registryAccess);
        } else {
            ToolTrimsCompat.toolTrimsCompat(itemStack, trim.material(), registryAccess, random);
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
