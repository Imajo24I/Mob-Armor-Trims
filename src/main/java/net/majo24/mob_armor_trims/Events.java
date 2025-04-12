package net.majo24.mob_armor_trims;

//? if fabric {
//? if >1.20.1 {
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
//?} else {
/*import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
*///?}
//?} else {
/*import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.event.LootTableLoadEvent;
*///?}

public class Events {
    private Events() {
    }

    //? if fabric {

    public static void registerEvents() {
        //? if >1.20.1 {
        LootTableEvents.MODIFY.register((key, builder, source, registries) -> {
            builder.apply(TrimLootTablesFunction.builder().build());
        });
        //?} else {
        /*LootTableEvents.MODIFY.register((resourceManager, lootDataManager, resourceLocation, builder, registryAccess) -> {
            builder.apply(TrimLootTablesFunction.builder().build());
        });
        *///?}
    }
    //?} else {
    /*public static void registerEvents(LootTableLoadEvent event) {
        LootTable table = event.getTable();

        table.functions = ImmutableList.<LootItemFunction>builder()
                .addAll(table.functions)
                .add(TrimLootTablesFunction.builder().build())
                .build();
    }
    *///?}
}
