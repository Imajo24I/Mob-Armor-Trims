package net.majo24.naturally_trimmed.mixin;

import net.majo24.naturally_trimmed.ToolTrimsCompat;
import net.majo24.naturally_trimmed.TrimApplier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.majo24.naturally_trimmed.config.Config.CONFIG_MANAGER;

@Mixin(AbstractVillager.class)
public abstract class VillagerTradesMixin extends Mob implements VillagerDataHolder {
    protected VillagerTradesMixin(EntityType<? extends Mob> arg, Level arg2) {
        super(arg, arg2);
    }

    @Inject(method = "addOffersFromItemListings", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffers;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER))
    private void trimTrades(MerchantOffers merchantOffers, VillagerTrades.ItemListing[] itemListings, int i, CallbackInfo ci) {
        if (!CONFIG_MANAGER.instance().enableTrimTrades || this.getType() == EntityType.WANDERING_TRADER) return;

        int minLevel = CONFIG_MANAGER.instance().trimTrades.minLevel;

        //? if >=1.21.5 {
        if (this.getVillagerData().level()  < minLevel) return;
        //?} else {
        /*if (this.getVillagerData().getLevel()  < minLevel) return;
        *///?}

        int trimChance = CONFIG_MANAGER.instance().trimTrades.trimChance;
        if (trimChance > random.nextInt(100)) return;

        ItemStack trade = merchantOffers.get(merchantOffers.size() - 1).getResult();

        if (!trade.is(ItemTags.TRIMMABLE_ARMOR) && !trade.is(ToolTrimsCompat.TRIMMABLE_TOOL_TAG)) return;

        Level level = this.level();
        RandomSource random = level.getRandom();
        RegistryAccess registryAccess = level.registryAccess();

        if (trade.is(ItemTags.TRIMMABLE_ARMOR)) {
            TrimApplier.applyRandomTrimToItem(trade, registryAccess, random);
        } else {
            ToolTrimsCompat.toolTrimsCompat(trade, registryAccess, random);
        }
    }
}
