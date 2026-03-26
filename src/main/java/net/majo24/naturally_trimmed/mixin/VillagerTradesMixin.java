package net.majo24.naturally_trimmed.mixin;

import net.majo24.naturally_trimmed.trim_application.ToolTrimsCompat;
import net.majo24.naturally_trimmed.trim_application.TrimApplier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
//? if >=26.1 {
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.loot.LootContext;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//?} else {
/*import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.injection.ModifyArg;
*///?}

import static net.majo24.naturally_trimmed.config.Config.CONFIG_MANAGER;

@Mixin(AbstractVillager.class)
public abstract class VillagerTradesMixin extends Mob {
    protected VillagerTradesMixin(EntityType<? extends Mob> arg, Level arg2) {
        super(arg, arg2);
    }

    @Unique
    private void trimTrade(ItemStack trade) {
        if (CONFIG_MANAGER.instance().trimTrades.trimChance < this.random.nextInt(100)) return;
        if (!trade.is(ItemTags.TRIMMABLE_ARMOR) && !trade.is(ToolTrimsCompat.TRIMMABLE_TOOLS_TAG)) return;

        // === Apply a trim to the trade ===
        RegistryAccess registryAccess = this.level().registryAccess();
        ArmorTrim trim = TrimApplier.getRandomTrim(registryAccess, this.random);
        if (trim == null) return;

        if (trade.is(ItemTags.TRIMMABLE_ARMOR)) {
            TrimApplier.applyTrim(trade, trim, registryAccess);
        } else {
            ToolTrimsCompat.applyTrimToTool(trade, trim.material(), registryAccess, this.random);
        }
    }

    //? if >=26.1 {
    @WrapOperation(method = "addOffersFromTradeSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/villager/AbstractVillager;addOffersFromItemListings(Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/trading/MerchantOffers;Lnet/minecraft/core/HolderSet;I)V"))
    private void trimTrades(LootContext lootContext, MerchantOffers offers, HolderSet<VillagerTrade> potentialOffers, int numberOfOffers, Operation<Object> operation) {
        operation.call(lootContext, offers, potentialOffers, numberOfOffers);

        if (!CONFIG_MANAGER.instance().enableTrimTrades) return;
        if (this instanceof VillagerDataHolder dataHolder && dataHolder.getVillagerData().level() < CONFIG_MANAGER.instance().trimTrades.minLevel) return;

        offers.subList(offers.size() - numberOfOffers, offers.size()).forEach(offer -> {
            trimTrade(offer.getResult());
        });
    }

    @WrapOperation(method = "addOffersFromTradeSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/villager/AbstractVillager;addOffersFromItemListingsWithoutDuplicates(Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/trading/MerchantOffers;Lnet/minecraft/core/HolderSet;I)V"))
    private void trimTradesWithoutDuplicates(LootContext lootContext, MerchantOffers offers, HolderSet<VillagerTrade> potentialOffers, int numberOfOffers, Operation<Object> operation) {
        operation.call(lootContext, offers, potentialOffers, numberOfOffers);

        if (!CONFIG_MANAGER.instance().enableTrimTrades) return;
        if (this instanceof VillagerDataHolder dataHolder && dataHolder.getVillagerData().level() < CONFIG_MANAGER.instance().trimTrades.minLevel) return;

        offers.subList(offers.size() - numberOfOffers, offers.size()).forEach(offer -> {
            trimTrade(offer.getResult());
        });
    }
    //?} else {
    /*@ModifyArg(method = "addOffersFromItemListings", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffers;add(Ljava/lang/Object;)Z"))
    private Object trimTrades(Object offer) {
        // === Check if a trim should be applied to the trade ===
        if (!CONFIG_MANAGER.instance().enableTrimTrades) return offer;
        if (CONFIG_MANAGER.instance().trimTrades.trimChance < this.random.nextInt(100)) return offer;

        int minLevel = CONFIG_MANAGER.instance().trimTrades.minLevel;
        if (this instanceof VillagerDataHolder dataHolder) {
            //? if >=1.21.5 {
            if (dataHolder.getVillagerData().level() < minLevel) return offer;
            //?} else
            //if (dataHolder.getVillagerData().getLevel()  < minLevel) return offer;
        }

        trimTrade(((MerchantOffer) offer).getResult());
        return offer;
    }
    *///?}
}
