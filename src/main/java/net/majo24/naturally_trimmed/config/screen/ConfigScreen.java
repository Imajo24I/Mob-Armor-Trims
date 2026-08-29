package net.majo24.naturally_trimmed.config.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.majo24.naturally_trimmed.config.Config;
import net.majo24.naturally_trimmed.config.FilterRule;
import net.majo24.naturally_trimmed.trim_application.TrimApplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.equipment.trim.*;
import org.jetbrains.annotations.NotNull;


import java.util.*;

import static net.majo24.naturally_trimmed.config.Config.INSTANCE;
import static net.majo24.naturally_trimmed.config.Config.DEFAULT;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.Component.literal;

public class ConfigScreen {
    private ConfigScreen() {
    }

    private static final PercentageFormatter percentageFormatter = new PercentageFormatter();
    private static final MissingTextureFilteringFormatter missingTextureFilteringFormatter = new MissingTextureFilteringFormatter();
    private static final String TRANSLATION_KEY_PREFIX = "naturally_trimmed.config.";
    private static final String TRANSLATION_KEY_DESCRIPTION = ".desc";

    public static Screen getConfigScreen(Screen parent) {
        YetAnotherConfigLib.Builder configScreen = YetAnotherConfigLib.createBuilder()
                .title(literal("Naturally Trimmed"))
                .save(INSTANCE::saveToFile)

                .category(buildGeneralCategory())
                .category(buildFilteringCategory())
                .category(buildUtilsCategory());

        return configScreen.build().generateScreen(parent);
    }

    private static ConfigCategory buildGeneralCategory() {
        return ConfigCategory.createBuilder()
                .name(prefixed("general"))
                .tooltip(desc("general"))

                .group(OptionGroup.createBuilder()
                        .name(prefixed("enable_features"))
                        .description(optionDesc("enable_features"))
                        .collapsed(true)

                        .option(Option.<Boolean>createBuilder()
                                .name(prefixed("enable_features.enableTrimMobs"))
                                .description(optionDesc("enable_features.enableTrimMobs"))
                                .binding(DEFAULT.enableTrimMobs,
                                        () -> INSTANCE.enableTrimMobs,
                                        enableTrimMobs -> INSTANCE.enableTrimMobs = enableTrimMobs)
                                .controller(BooleanControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(prefixed("enable_features.enableTrimLootTables"))
                                .description(optionDesc("enable_features.enableTrimLootTables"))
                                .binding(DEFAULT.enableTrimLootTables,
                                        () -> INSTANCE.enableTrimLootTables,
                                        enableTrimLootTables -> INSTANCE.enableTrimLootTables = enableTrimLootTables)
                                .controller(BooleanControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(prefixed("enable_features.enableTrimTrades"))
                                .description(optionDesc("enable_features.enableTrimTrades"))
                                .binding(DEFAULT.enableTrimTrades,
                                        () -> INSTANCE.enableTrimTrades,
                                        enableTrimTrades -> INSTANCE.enableTrimTrades = enableTrimTrades)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())

                .group(buildTrimMobsGroup())

                .group(OptionGroup.createBuilder()
                        .name(prefixed("trimLootTables"))
                        .description(optionDesc("trimLootTables"))
                        .collapsed(true)

                        .option(Option.<Integer>createBuilder()
                                .name(prefixed("trimLootTables.trimChance"))
                                .description(optionDesc("trimLootTables.trimChance"))
                                .binding(DEFAULT.trimLootTables.trimChance,
                                        () -> INSTANCE.trimLootTables.trimChance,
                                        trimChance -> INSTANCE.trimLootTables.trimChance = trimChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(percentageFormatter))
                                .build()
                        ).build())

                .group(OptionGroup.createBuilder()
                        .name(prefixed("trimTrades"))
                        .description(optionDesc("trimTrades"))
                        .collapsed(true)

                        .option(Option.<Integer>createBuilder()
                                .name(prefixed("trimTrades.trimChance"))
                                .description(optionDesc("trimTrades.trimChance"))
                                .binding(DEFAULT.trimTrades.trimChance,
                                        () -> INSTANCE.trimTrades.trimChance,
                                        trimChance -> INSTANCE.trimTrades.trimChance = trimChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(percentageFormatter))
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(prefixed("trimTrades.minLevel"))
                                .description(optionDesc("trimTrades.minLevel"))
                                .binding(DEFAULT.trimTrades.minLevel,
                                        () -> INSTANCE.trimTrades.minLevel,
                                        minLevel -> INSTANCE.trimTrades.minLevel = minLevel)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(1, 5)
                                        .step(1))
                                .build())
                        .build())
                .build();
    }

    private static OptionGroup buildTrimMobsGroup() {
        return OptionGroup.createBuilder()
                .name(prefixed("trimMobs"))
                .description(optionDesc("trimMobs"))
                .collapsed(true)

                .option(Option.<Integer>createBuilder()
                        .name(prefixed("trimMobs.trimChance"))
                        .description(optionDesc("trimMobs.trimChance"))
                        .binding(DEFAULT.trimMobs.trimChance,
                                () -> INSTANCE.trimMobs.trimChance,
                                val -> INSTANCE.trimMobs.trimChance = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(percentageFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(prefixed("trimMobs.pieceTrimChance"))
                        .description(optionDesc("trimMobs.pieceTrimChance"))
                        .binding(DEFAULT.trimMobs.pieceTrimChance,
                                () -> INSTANCE.trimMobs.pieceTrimChance,
                                val -> INSTANCE.trimMobs.pieceTrimChance = val)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(percentageFormatter))
                        .build())
                .build();
    }

    private static ConfigCategory buildFilteringCategory() {
        return ConfigCategory.createBuilder()
                .name(prefixed("filtering"))
                .tooltip(desc("filtering"))

                .option(Option.<Config.MissingTextureFiltering>createBuilder()
                        .name(prefixed("filtering.missingTextureFiltering"))
                        .description(optionDesc("filtering.missingTextureFiltering"))
                        .binding(DEFAULT.trimFiltering.missingTextureFiltering,
                                () -> INSTANCE.trimFiltering.missingTextureFiltering,
                                val -> INSTANCE.trimFiltering.missingTextureFiltering = val)
                        .controller(opt -> EnumControllerBuilder.create(opt)
                                .enumClass(Config.MissingTextureFiltering.class)
                                .formatValue(missingTextureFilteringFormatter))
                        .build()
                )

                .option(Option.<Boolean>createBuilder()
                        .name(prefixed("filtering.vanillaOnly"))
                        .description(optionDesc("filtering.vanillaOnly"))
                        .binding(DEFAULT.trimFiltering.vanillaOnly,
                                () -> INSTANCE.trimFiltering.vanillaOnly,
                                vanillaOnly -> INSTANCE.trimFiltering.vanillaOnly = vanillaOnly)
                        .controller(BooleanControllerBuilder::create)
                        .build())

                .option(ButtonOption.createBuilder()
                        .name((prefixed("filtering.trimFilter")))
                        .description(optionDesc("filtering.trimFilter"))
                        .text(prefixed("utils.run"))
                        .action((screen, option) -> Util.getPlatform().openPath(NaturallyTrimmed.getConfigPath()))
                        .build())
                .build();
    }

    private static ConfigCategory buildUtilsCategory() {
        boolean isInWorld = Minecraft.getInstance().level != null;

        return ConfigCategory.createBuilder()
                .name(prefixed("utils"))
                .tooltip(desc("utils"))

                .option(ButtonOption.createBuilder()
                        .name((prefixed("utils.openFile")))
                        .description(optionDesc("utils.openFile"))
                        .text(prefixed("utils.run"))
                        .action((screen, option) -> Util.getPlatform().openPath(NaturallyTrimmed.getConfigPath()))
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(prefixed("utils.reloadConfig"))
                        .description(optionDesc("utils.reloadConfig"))
                        .text(prefixed("utils.run"))
                        .action((screen, option) -> {
                            INSTANCE.loadFromFile();
                            screen.onClose();
                        })
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(prefixed("utils.validateTrimFilter"))
                        .description(optionDesc("utils.validateTrimFilter"))
                        .text(isInWorld ? prefixed("utils.run") : prefixed("utils.run").withStyle(ChatFormatting.STRIKETHROUGH))
                        .action((screen, option) -> validateFilter())
                        .build())
                .build();
    }

    private static MutableComponent prefixed(String path) {
        return translatable(TRANSLATION_KEY_PREFIX + path);
    }

    private static MutableComponent desc(String path) {
        return translatable(TRANSLATION_KEY_PREFIX + path + TRANSLATION_KEY_DESCRIPTION);
    }

    private static OptionDescription optionDesc(String path) {
        return OptionDescription.of(desc(path));
    }

    public static void validateFilter() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || player == null) return;
        RegistryAccess registryAccess = level.registryAccess();

        message(player, literal("\nValidating trim filter...\n").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.BOLD));

        List<FilterRule> filter = INSTANCE.trimFiltering.trimFilter;
        List<Holder.Reference<TrimMaterial>> materials = TrimApplier.getTrimMaterials(registryAccess);
        List<Holder.Reference<TrimPattern>> patterns = TrimApplier.getTrimPatterns(registryAccess);
        List<ArmorTrim> trims = materials.stream().flatMap(material -> patterns.stream().map(pattern -> new ArmorTrim(material, pattern))).toList();

        Map<ArmorTrim, Optional<FilterRule>> trimStates = new HashMap<>();
        List<FilterRule> unusedRules = new ArrayList<>(filter);

        for (ArmorTrim trim : trims) {
            Optional<FilterRule> relevantRule = Optional.empty();

            for (FilterRule rule : filter) {
                if (rule.matches(trim) != FilterRule.Result.Irrelevant) {
                    unusedRules.remove(rule);
                    relevantRule = Optional.of(rule);
                    break;
                }
            }

            trimStates.put(trim, relevantRule);
        }

        message(player, literal("Trim -> State -> Rule (formatted without direction)").withStyle(ChatFormatting.UNDERLINE));
        for (Map.Entry<ArmorTrim, Optional<FilterRule>> state : trimStates.entrySet()) {
            String filteredState = "Whitelisted";
            String ruleString = "whitelisted by default";

            if (state.getValue().isPresent()) {
                FilterRule rule = state.getValue().get();
                filteredState = rule.direction().toString() + "ed";
                ruleString = FilterRule.sourceToString(rule.materialSource()) + " / " + FilterRule.sourceToString(rule.patternSource());
            }

            message(player, literal(
                    state.getKey().material().unwrapKey().get().identifier()
                            + " / "
                            + state.getKey().pattern().unwrapKey().get().identifier()
                            + " -> " + filteredState + " -> " + ruleString
            ));
        }

        message(player, literal("\nChecking for unused rules...").withStyle(ChatFormatting.UNDERLINE));
        for (FilterRule rule : unusedRules) {
            message(player, literal(rule.toString()));
        }
        message(player, literal("Done checking for unused rules").withStyle(ChatFormatting.UNDERLINE));

        message(player, literal("\nNote that due to minecrafts chat history length limitation, the log of the validation will likely not be fully visible. See the log file for the full validation log"));

        message(player, literal("\nDone validating trim filter...\n").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.BOLD));
    }

    public static class PercentageFormatter implements ValueFormatter<Integer> {
        @Override
        public Component format(Integer value) {
            return literal(value + "%");
        }
    }

    public static class MissingTextureFilteringFormatter implements ValueFormatter<Config.MissingTextureFiltering> {
        @Override
        public Component format(Config.MissingTextureFiltering value) {
            return switch (value) {
                case TEXTURE_VALIDATION -> prefixed("filtering.missingTextureFiltering.textureValidation");
                case PRECAUTIONARY -> prefixed("filtering.missingTextureFiltering.precautionary");
                case NONE -> prefixed("filtering.missingTextureFiltering.none");
            };
        }
    }

    static class BackupScreen extends OptionsSubScreen {
        public BackupScreen(Screen parent) {
            super(parent, Minecraft.getInstance().options, literal("Naturally Trimmed"));
        }

        @Override
        public void init() {
            MultiLineTextWidget messageWidget = new MultiLineTextWidget(
                    width / 2 - 110, height / 2 - 40,
                    translatable("naturally_trimmed.config.backup_screen.installYACL"),
                    Objects.requireNonNull(minecraft).font);
            messageWidget.setMaxWidth(240);
            messageWidget.setCentered(true);
            addRenderableWidget(messageWidget);

            Button openLinkButton = Button.builder(translatable("naturally_trimmed.config.backup_screen.viewOnModrinth"),
                            button -> minecraft.setScreen(new ConfirmLinkScreen(
                                    open -> {
                                        if (open) Util.getPlatform().openUri("https://modrinth.com/mod/yacl");
                                        minecraft.setScreen(lastScreen);
                                    }, "https://modrinth.com/mod/yacl", true)))
                    .pos(width / 2 - 120, height / 2)
                    .size(115, 20)
                    .build();
            addRenderableWidget(openLinkButton);

            Button exitButton = Button.builder(CommonComponents.GUI_OK,
                            button -> onClose())
                    .pos(width / 2 + 5, height / 2)
                    .size(115, 20)
                    .build();
            addRenderableWidget(exitButton);
        }

        @Override
        protected void addOptions() {
        }

        @Override
                //? if >=26.1 {
        public void extractRenderState(@NotNull net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
         //?} else
        //public void render(@NotNull net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            //? if >=26.1 {
            super.extractRenderState(graphics, mouseX, mouseY, delta);
            graphics.centeredText(font, title, width / 2, 5, 0xffffff);
            //?} else {
            /*super.render(graphics, mouseX, mouseY, delta);
            graphics.drawCenteredString(font, title, width / 2, 5, 0xffffff);
            *///?}
        }
    }

    private static void message(LocalPlayer player, Component message) {
        //? if >=26.1 {
        player.sendSystemMessage(message);
         //?} else
        //player.displayClientMessage(message, false);
    }
}