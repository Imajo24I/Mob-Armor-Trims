package net.majo24.naturally_trimmed;

import net.majo24.naturally_trimmed.trim_application.TrimLootTablesFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

//? if fabric {
import net.minecraft.core.Registry;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
//?} else if neoforge {
/*import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?} else {
/*import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
*///?}



public class RegistryHelper {
    private RegistryHelper() {}

    //? if fabric {
    public static final LootItemFunctionType TRIM_LOOT_TABLES_FUNCTION = Registry.register(
            BuiltInRegistries.LOOT_FUNCTION_TYPE,
            Objects.requireNonNull(ResourceLocation.tryBuild(NaturallyTrimmed.MOD_ID, "trim_loot_tables_function")),
            //? if >1.20.1 {
            new LootItemFunctionType(TrimLootTablesFunction.CODEC));
            //?} else {
            /*new LootItemFunctionType(new TrimLootTablesFunction.Serializer()));
            *///?}

    //?} else if neoforge {
    /*//? if >1.20.4 {
    public static final DeferredRegister<LootItemFunctionType<?>> FUNCTION_REGISTER =
    //?} else {
    /^public static final DeferredRegister<LootItemFunctionType> FUNCTION_REGISTER =
     ^///?}
            DeferredRegister.create(
                    BuiltInRegistries.LOOT_FUNCTION_TYPE,
                    NaturallyTrimmed.MOD_ID
            );
    //? if >1.20.4 {
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<?>> TRIM_LOOT_TABLES_FUNCTION =
    //?} else {
    /^public static final DeferredHolder<LootItemFunctionType, LootItemFunctionType> TRIM_LOOT_TABLES_FUNCTION =
     ^///?}
            FUNCTION_REGISTER.register(
                    "trim_loot_tables_function",
                    () -> new LootItemFunctionType(TrimLootTablesFunction.CODEC)
            );
    *///?} else {
    /*public static final DeferredRegister<LootItemFunctionType> FUNCTION_REGISTER = DeferredRegister.create(
            Registries.LOOT_FUNCTION_TYPE,
            NaturallyTrimmed.MOD_ID
    );

    public static final RegistryObject<LootItemFunctionType> TRIM_LOOT_TABLES_FUNCTION = FUNCTION_REGISTER.register(
            "trim_loot_tables_function",
            () -> new LootItemFunctionType(new TrimLootTablesFunction.Serializer())
    );
    *///?}


    /**
     * Call this method to make java initialize this class.
     * Used to register to Registries before they are frozen
     */
    public static void initClass() {
    }
}
