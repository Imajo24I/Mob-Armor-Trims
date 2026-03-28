package net.majo24.naturally_trimmed;

import net.majo24.naturally_trimmed.trim_application.TrimLootTablesFunction;

//? if fabric {
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
//?} else {
/*import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import com.google.common.collect.ImmutableList;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
*///?}


public class Events {
    private Events() {}

    public static void registerEvents() {
        //? if fabric {
        addTrimFunctionToLootTables();
        //?} else
        //NeoForge.EVENT_BUS.addListener(Events::addTrimFunctionToLootTables);
    }

    //? if fabric {
    public static void addTrimFunctionToLootTables() {
        LootTableEvents.MODIFY.register((key, builder, source, registries) -> builder.apply(TrimLootTablesFunction.builder().build()));
    }
    //?} else {
    /*public static void addTrimFunctionToLootTables(LootTableLoadEvent event) {
        LootTable table = event.getTable();

        table.functions = ImmutableList.<LootItemFunction>builder()
                .addAll(table.functions)
                .add(TrimLootTablesFunction.builder().build())
                .build();

        table.compositeFunction = LootItemFunctions.compose(table.functions);
    }
    *///?}
}
