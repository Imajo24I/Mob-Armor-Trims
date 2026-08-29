package net.majo24.naturally_trimmed.trim_application;

import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.majo24.naturally_trimmed.config.Config;
import net.majo24.naturally_trimmed.config.FilterRule;

import static net.majo24.naturally_trimmed.NaturallyTrimmed.LOGGER;
import static net.majo24.naturally_trimmed.NaturallyTrimmed.isModLoaded;
import static net.majo24.naturally_trimmed.config.Config.INSTANCE;

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
        List<Holder.Reference<TrimMaterial>> trimMaterials = getTrimMaterials(registryAccess);
        List<Holder.Reference<TrimPattern>> trimPatterns = getTrimPatterns(registryAccess);
        if (INSTANCE.trimFiltering.vanillaOnly) {
            trimMaterials.removeIf(material -> !material.key().identifier().getNamespace().equals("minecraft"));
            trimPatterns.removeIf(pattern -> !pattern.key().identifier().getNamespace().equals("minecraft"));
        }

        List<FilterRule> filter = INSTANCE.trimFiltering.trimFilter;

        // Falls back to PRECAUTIONARY, if TEXTURE_VALIDATION is not possible in the current environment
        if (INSTANCE.trimFiltering.missingTextureFiltering.equals(Config.MissingTextureFiltering.TEXTURE_VALIDATION) && NaturallyTrimmed.isClientAvailable) {
            // === Texture Validation filtering ===
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
                if (!FilterRule.isTrimBlacklistedByFilter(filter, trim) && isValidTrim(atlas, missingSprite, trim, material)) {
                    return trim;
                }
            }
        } else if (!INSTANCE.trimFiltering.missingTextureFiltering.equals(Config.MissingTextureFiltering.NONE)) {
            // === Precautionary trim filtering ===
            // Ensure no trim patterns added by elytra trims are used
            if (isModLoaded("elytratrims")) {
                trimPatterns.removeIf(pattern -> !isModLoaded(pattern.key().identifier().getNamespace()) || pattern.key().identifier().getNamespace().equals("elytratrims"));
            }

            List<ArmorTrim> trims = Util.toShuffledList(trimMaterials.stream().flatMap(material -> trimPatterns.stream().map(pattern -> new ArmorTrim(material, pattern))), random);
            for (ArmorTrim trim : trims) {
                if ((((Holder.Reference<?>) trim.material()).key().identifier().getNamespace().equals("minecraft")
                        || ((Holder.Reference<?>) trim.pattern()).key().identifier().getNamespace().equals("minecraft"))
                        && !FilterRule.isTrimBlacklistedByFilter(filter, trim)) {
                    return trim;
                }
            }
        } else {
            // === No additional filtering for missing trims ===
            List<ArmorTrim> trims = Util.toShuffledList(trimMaterials.stream().flatMap(material -> trimPatterns.stream().map(pattern -> new ArmorTrim(material, pattern))), random);
            for (ArmorTrim trim : trims) {
                if (!FilterRule.isTrimBlacklistedByFilter(filter, trim)) {
                    return trim;
                }
            }
        }

        throw noViableTrim;
    }

    /**
     * Runs the selected Trim System on the armor of the entity. Also applies trims to the entity's equipment, if possible.
     */
    public static void trimEquipment(LivingEntity entity) {
        if (!INSTANCE.enableTrimMobs) return;
        if (INSTANCE.trimMobs.noTrimsChance >= entity.getRandom().nextInt(100)) return;

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

        ArmorTrim trim;
        try {
            trim = getRandomTrim(registryAccess, random, armor);
        } catch (NoSuchElementException err) {
            LOGGER.warn(err.getMessage());
            return;
        }

        // Apply trim to the armor
        for (ItemStack armorPiece : armor) {
            if (INSTANCE.trimMobs.trimChance >= random.nextInt(100)) {
                applyTrim(armorPiece, trim);
            }
        }

        // Apply trim to the equipment, if possible
        if ((NaturallyTrimmed.isModLoaded(ToolTrimsCompat.TOOL_TRIMS_ID) || NaturallyTrimmed.isModLoaded(ToolTrimsCompat.TRIMMABLE_TOOLS_ID))
                && INSTANCE.trimMobs.trimChance >= random.nextInt(100)) {
            ToolTrimsCompat.applyTrimToTool(entity.getMainHandItem(), entity.level().registryAccess(), random);
        }
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
}
