package net.majo24.naturally_trimmed.trim_application;

import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.majo24.naturally_trimmed.config.Config.TrimMobsSubConfig.TrimSystem;
import net.majo24.naturally_trimmed.config.FilterRule;

import static net.majo24.naturally_trimmed.NaturallyTrimmed.LOGGER;
import static net.majo24.naturally_trimmed.NaturallyTrimmed.isModLoaded;
import static net.majo24.naturally_trimmed.config.Config.CONFIG_MANAGER;

//? if >=1.21.11 {
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.ResourceKey;
//?} else {
/*import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
*///?}

import net.minecraft.IdentifierException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.item.ItemStack;

//? if >=1.21.11
import net.minecraft.world.entity.EquipmentSlotGroup;

import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;

public class TrimApplier {
    private TrimApplier() {
    }

    private static final NoSuchElementException noViableTrim = new NoSuchElementException("Couldn't find viable armor trim. Please check configuration (mods, datapacks and Naturally Trimmed config settings) for potential issues. Skipping trim application.");

    /**
     * Applies the given armor trim onto the given itemStack
     */
    public static void applyTrim(ItemStack itemStack, ArmorTrim armorTrim) {
        itemStack.applyComponents(DataComponentPatch.builder().set(DataComponents.TRIM, armorTrim).build());
    }

    /**
     * Returns a random but filtered armor trim. The filter is to avoid missing-texture textures. <p>
     * On clientside:
     * <p>
     * The mod will validate the trim by checking for an according texture in the armor trims texture atlas (only possible on the client)
     * <p>
     * On serverside:
     * <ul>
     *   <li>Ensures at least one of the two trim parts is non-modded (a mod's trim parts are only rarely compatible with another mod's trim parts)</li>
     *   <li>Ensures the trim pattern isn't added by the elytra trims mod, as elytra trims 4.5 adds patterns from some other mods to the registry even though these mods may not be loaded. This causes the missing-texture texture since elytra trims only adds an elytra-compatible version of the trim.</li>
     *   <li>Ensures the trim material and pattern aren't blacklisted by the mods blacklists (default is blacklisting tooltrims patterns, as they're only compatible with tools)</li>
     * </ul>
     */
    public static ArmorTrim getRandomTrim(RegistryAccess registryAccess, RandomSource random, List<ItemStack> armorPieces) throws NoSuchElementException {
        List<Holder.Reference<TrimMaterial>> trimMaterials = getFilteredTrimMaterials(registryAccess);
        List<Holder.Reference<TrimPattern>> trimPatterns = getFilteredTrimPatterns(registryAccess);

        if (NaturallyTrimmed.isClientAvailable && CONFIG_MANAGER.instance().trimFiltering.textureValidationFiltering) {
            // === Texture Validation Filtering ===
            List<ArmorTrim> trims = Util.toShuffledList(trimMaterials.stream().flatMap(material -> trimPatterns.stream().map(pattern -> new ArmorTrim(material, pattern))), random);

            //? if 1.21.1 {
            /*TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
            TextureAtlasSprite missingSprite = atlas.getSprite(Identifier.tryBuild("naturally_trimmed", "placeholder"));
            Set<Holder<ArmorMaterial>> material = armorPieces.stream().map(piece -> ((ArmorItem) piece.getItem()).getMaterial()).collect(Collectors.toSet());
            *///?} else {
            TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.ARMOR_TRIMS);
            TextureAtlasSprite missingSprite = atlas.missingSprite();
            Set<ResourceKey<EquipmentAsset>> material = armorPieces.stream().map(piece -> (piece.get(DataComponents.EQUIPPABLE)).assetId().orElseThrow(() -> noViableTrim)).collect(Collectors.toSet());
            //?}

            for (ArmorTrim trim : trims) {
                if (isValidTrim(atlas, missingSprite, trim, material)) {
                    return trim;
                }
            }

            throw noViableTrim;

        } else {
            // === Precautionary Trim Filtering ===
            Holder.Reference<TrimMaterial> trimMaterial;
            Holder.Reference<TrimPattern> trimPattern;

            // Ensure no trim patterns added by elytra trims are used
            trimPatterns.removeIf(pattern -> (isModLoaded("elytratrims") && (!isModLoaded(pattern.key().identifier().getNamespace()) || pattern.key().identifier().getNamespace().equals("elytratrims"))));

            if (trimMaterials.isEmpty() || trimPatterns.isEmpty()) throw noViableTrim;

            // Ensure at least one of the two trim parts is non-modded
            do {
                trimMaterial = Util.getRandom(trimMaterials, random);
                trimPattern = Util.getRandom(trimPatterns, random);
            } while (!trimMaterial.key().identifier().getNamespace().equals("minecraft") && !trimPattern.key().identifier().getNamespace().equals("minecraft"));

            return new ArmorTrim(trimMaterial, trimPattern);
        }
    }

    /**
     * Runs the selected Trim System on the armor of the entity. Also applies trims to the entity's equipment, if possible.
     */
    public static void trimEquipment(LivingEntity entity) {
        if (!CONFIG_MANAGER.instance().enableTrimMobs) return;
        if (CONFIG_MANAGER.instance().trimMobs.noTrimsChance >= entity.getRandom().nextInt(100)) return;

        //? if >=1.21.11 {
        List<ItemStack> armor = EquipmentSlotGroup.ARMOR.slots().stream()
                .map(entity::getItemBySlot)
                .filter(armorPiece -> !armorPiece.isEmpty() && armorPiece.is(ItemTags.TRIMMABLE_ARMOR))
                .toList();
        //?} else {
        /*List<ItemStack> armor = java.util.stream.StreamSupport.stream(entity.getArmorSlots().spliterator(), false)
                .filter(armorPiece -> !armorPiece.isEmpty() && armorPiece.is(ItemTags.TRIMMABLE_ARMOR))
                .toList();
        *///?}

        if (armor.isEmpty()) return;

        RandomSource random = entity.getRandom();
        RegistryAccess registryAccess = entity.level().registryAccess();

        TrimSystem enabledSystem = CONFIG_MANAGER.instance().trimMobs.trimSystem;

        ArmorTrim trim;
        try {
            trim = (enabledSystem == TrimSystem.RANDOM_TRIMS)
                    ? getRandomTrim(registryAccess, random, armor)
                    : getPredefinedTrim(registryAccess, random);
        } catch (NoSuchElementException err) {
            LOGGER.warn(err.getMessage());
            return;
        }

        // Apply trim to the armor
        for (ItemStack armorPiece : armor) {
            if (CONFIG_MANAGER.instance().trimMobs.trimChance >= random.nextInt(100)) {
                applyTrim(armorPiece, trim);
            }
        }

        // Apply trim to the equipment, if possible
        if ((NaturallyTrimmed.isModLoaded(ToolTrimsCompat.TOOL_TRIMS_ID) || NaturallyTrimmed.isModLoaded(ToolTrimsCompat.TRIMMABLE_TOOLS_ID))
                && CONFIG_MANAGER.instance().trimMobs.trimChance >= random.nextInt(100)) {
            ToolTrimsCompat.applyTrimToTool(entity.getMainHandItem(), entity.level().registryAccess(), random);
        }
    }

    public static List<Holder.Reference<TrimPattern>> getFilteredTrimPatterns(RegistryAccess registryAccess) {
        List<Holder.Reference<TrimPattern>> trimPatterns = getTrimPatterns(registryAccess);

        // === Pattern Filters ===
        List<FilterRule<TrimPattern>> filter = CONFIG_MANAGER.instance().trimFiltering.patternFilter;
        trimPatterns.removeIf(pattern -> FilterRule.resolveFilterForBlacklisted(filter, pattern));

        // === Vanilla Only ===
        if (CONFIG_MANAGER.instance().trimFiltering.vanillaOnly) {
            trimPatterns.removeIf(pattern -> !pattern.key().identifier().getNamespace().equals("minecraft"));
        }

        return trimPatterns;
    }

    public static List<Holder.Reference<TrimMaterial>> getFilteredTrimMaterials(RegistryAccess registryAccess) {
        List<Holder.Reference<TrimMaterial>> trimMaterials = getTrimMaterials(registryAccess);

        // === Material Filters ===
        List<FilterRule<TrimMaterial>> filter = CONFIG_MANAGER.instance().trimFiltering.materialFilter;
        trimMaterials.removeIf(material -> FilterRule.resolveFilterForBlacklisted(filter, material));

        // === Vanilla Only ===
        if (CONFIG_MANAGER.instance().trimFiltering.vanillaOnly) {
            trimMaterials.removeIf(material -> !material.key().identifier().getNamespace().equals("minecraft"));
        }

        return trimMaterials;
    }

    public static List<Holder.Reference<TrimPattern>> getTrimPatterns(RegistryAccess registryAccess) {
        //? if >=1.21.11 {
        return new ArrayList<>(registryAccess.lookupOrThrow(Registries.TRIM_PATTERN).listElements().toList());
        //?} else
        //return new ArrayList<>(registryAccess.registryOrThrow(Registries.TRIM_PATTERN).holders().toList());
    }

    public static List<Holder.Reference<TrimMaterial>> getTrimMaterials(RegistryAccess registryAccess) {
        //? if >=1.21.11 {
        return new ArrayList<>(registryAccess.lookupOrThrow(Registries.TRIM_MATERIAL).listElements().toList());
        //?} else
        //return new ArrayList<>(registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).holders().toList());
    }

    /**
     * Validates the given trim by checking if all relevant textures exist
     * @return True if trim is valid
     */
    //? if 1.21.1 {
    /*private static boolean isValidTrim(TextureAtlas atlas, TextureAtlasSprite missingSprite, ArmorTrim trim, Set<Holder<ArmorMaterial>> materials) {
        for (Holder<ArmorMaterial> material : materials) {
            TextureAtlasSprite innerTexture = atlas.getSprite(trim.innerTexture(material));
            TextureAtlasSprite outerTexture = atlas.getSprite(trim.outerTexture(material));

            if (innerTexture.equals(missingSprite) || outerTexture.equals(missingSprite)) {
                return false;
            }
        }

        return true;
    }
    *///?} else {
    private static boolean isValidTrim(TextureAtlas atlas, TextureAtlasSprite missingSprite, ArmorTrim trim, Set<ResourceKey<EquipmentAsset>> equipmentAssets) {
        for (EquipmentClientInfo.LayerType layer : List.of(EquipmentClientInfo.LayerType.HUMANOID, EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS)) {
            for (ResourceKey<EquipmentAsset> equipmentAsset : equipmentAssets) {
                EquipmentLayerRenderer.TrimSpriteKey spriteKey = new EquipmentLayerRenderer.TrimSpriteKey(trim, layer, equipmentAsset);
                TextureAtlasSprite texture = atlas.getSprite(spriteKey.spriteId());

                if (texture.contents().equals(missingSprite.contents())) {
                    return false;
                }
            }
        }

        return true;
    }
    //?}

    public static ArmorTrim getPredefinedTrim(RegistryAccess registryAccess, RandomSource random) throws NoSuchElementException {
        List<TrimData> predefinedTrims = CONFIG_MANAGER.instance().trimMobs.predefinedTrims;
        Util.shuffle(predefinedTrims, random);

        for (TrimData predefinedTrim : predefinedTrims) {
            try {
                return predefinedTrim.getTrim(registryAccess);
            } catch (NoSuchElementException | IdentifierException e) {
                NaturallyTrimmed.LOGGER.error("Failed to load predefined trim '{}' - '{}'. Please ensure this is a valid trim.", predefinedTrim.material(), predefinedTrim.pattern(), e);
            }
        }

        throw noViableTrim;
    }
}
