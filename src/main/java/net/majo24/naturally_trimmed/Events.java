package net.majo24.naturally_trimmed;

import net.majo24.naturally_trimmed.trim_application.TrimLootTablesFunction;

//? if fabric {
//? if >1.20.6 {
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
//?} else {
/*import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
*///?}
//?} else {
/*import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
//? if neoforge {
/^import com.google.common.collect.ImmutableList;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
^///?} else {
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import org.apache.commons.lang3.ArrayUtils;
//?}
*///?}


public class Events {
    private Events() {}

    public static void registerEvents() {
        //? if fabric {
        addTrimFunctionToLootTables();
        //?} else if neoforge {
        /*NeoForge.EVENT_BUS.addListener(Events::addTrimFunctionToLootTables);
        *///?} else
        /*MinecraftForge.EVENT_BUS.addListener(Events::addTrimFunctionToLootTables);*/
    }

    //? if fabric {
    public static void addTrimFunctionToLootTables() {
        //? if >1.20.6 {
        LootTableEvents.MODIFY.register((key, builder, source, registries) -> builder.apply(TrimLootTablesFunction.builder().build()));
        //?} else if 1.20.6 {
        /*LootTableEvents.MODIFY.register((key, builder, source) -> builder.apply(TrimLootTablesFunction.builder().build()));
        *///?} else
        /*LootTableEvents.MODIFY.register((resourceManager, lootDataManager, resourceLocation, builder, registryAccess) -> builder.apply(TrimLootTablesFunction.builder().build()));*/
    }
    //?} else if neoforge {
    /*public static void addTrimFunctionToLootTables(LootTableLoadEvent event) {
        LootTable table = event.getTable();

        table.functions = ImmutableList.<LootItemFunction>builder()
                .addAll(table.functions)
                .add(TrimLootTablesFunction.builder().build())
                .build();

        table.compositeFunction = LootItemFunctions.compose(table.functions);
    }
    *///?} else {
    /*public static void addTrimFunctionToLootTables(LootTableLoadEvent event) {
        LootTable table = event.getTable();

        table.functions = ArrayUtils.add(table.functions, TrimLootTablesFunction.builder().build());
        table.compositeFunction = LootItemFunctions.compose(table.functions);
    }
    *///?}
}
