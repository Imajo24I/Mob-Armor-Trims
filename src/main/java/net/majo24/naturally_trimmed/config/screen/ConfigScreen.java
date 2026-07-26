package net.majo24.naturally_trimmed.config.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.majo24.naturally_trimmed.config.Config;
import net.majo24.naturally_trimmed.trim_application.TrimApplier;
import net.majo24.naturally_trimmed.trim_application.TrimData;
import net.majo24.naturally_trimmed.config.FilterRule;
import net.minecraft.ChatFormatting;
import net.minecraft.IdentifierException;
import net.minecraft.core.Holder;
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

import static net.majo24.naturally_trimmed.config.Config.CONFIG_MANAGER;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.Component.literal;

public class ConfigScreen {
    private ConfigScreen() {
    }

    public static final Formatters.Percentage percentageFormatter = new Formatters.Percentage();
    public static final Formatters.TrimSystem trimSystemFormatter = new Formatters.TrimSystem();

    public static Screen getConfigScreen(Screen parent) {
        YetAnotherConfigLib.Builder configScreen = YetAnotherConfigLib.createBuilder()
                .title(literal("Naturally Trimmed"))
                .save(CONFIG_MANAGER::saveInstance)

                .category(buildGeneralCategory())
                .category(buildFilteringCategory())
                .category(buildUtilsCategory());

        return configScreen.build().generateScreen(parent);
    }

    private static ConfigCategory buildGeneralCategory() {
        return ConfigCategory.createBuilder()
                .name(translatable("naturally_trimmed.config.general"))
                .tooltip(translatable("naturally_trimmed.config.general.desc"))

                .group(OptionGroup.createBuilder()
                        .name(translatable("naturally_trimmed.config.enable_features"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.enable_features.desc")))
                        .collapsed(true)

                        .option(Option.<Boolean>createBuilder()
                                .name(translatable("naturally_trimmed.config.enable_features.enableTrimMobs"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.enable_features.enableTrimMobs.desc")))
                                .binding(CONFIG_MANAGER.defaults().enableTrimMobs,
                                        () -> CONFIG_MANAGER.instance().enableTrimMobs,
                                        enableTrimMobs -> CONFIG_MANAGER.instance().enableTrimMobs = enableTrimMobs)
                                .controller(BooleanControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(translatable("naturally_trimmed.config.enable_features.enableTrimLootTables"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.enable_features.enableTrimLootTables.desc")))
                                .binding(CONFIG_MANAGER.defaults().enableTrimLootTables,
                                        () -> CONFIG_MANAGER.instance().enableTrimLootTables,
                                        enableTrimLootTables -> CONFIG_MANAGER.instance().enableTrimLootTables = enableTrimLootTables)
                                .controller(BooleanControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(translatable("naturally_trimmed.config.enable_features.enableTrimTrades"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.enable_features.enableTrimTrades.desc")))
                                .binding(CONFIG_MANAGER.defaults().enableTrimTrades,
                                        () -> CONFIG_MANAGER.instance().enableTrimTrades,
                                        enableTrimTrades -> CONFIG_MANAGER.instance().enableTrimTrades = enableTrimTrades)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())

                .group(buildTrimMobsGroup())

                .group(OptionGroup.createBuilder()
                        .name(translatable("naturally_trimmed.config.trimLootTables"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.trimLootTables.desc")))
                        .collapsed(true)

                        .option(Option.<Integer>createBuilder()
                                .name(translatable("naturally_trimmed.config.trimLootTables.trimChance"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.trimLootTables.trimChance.desc")))
                                .binding(CONFIG_MANAGER.defaults().trimLootTables.trimChance,
                                        () -> CONFIG_MANAGER.instance().trimLootTables.trimChance,
                                        trimChance -> CONFIG_MANAGER.instance().trimLootTables.trimChance = trimChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(percentageFormatter))
                                .build()
                        ).build())

                .group(OptionGroup.createBuilder()
                        .name(translatable("naturally_trimmed.config.trimTrades"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.trimTrades.desc")))
                        .collapsed(true)

                        .option(Option.<Integer>createBuilder()
                                .name(translatable("naturally_trimmed.config.trimTrades.trimChance"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.trimTrades.trimChance.desc")))
                                .binding(CONFIG_MANAGER.defaults().trimTrades.trimChance,
                                        () -> CONFIG_MANAGER.instance().trimTrades.trimChance,
                                        trimChance -> CONFIG_MANAGER.instance().trimTrades.trimChance = trimChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(percentageFormatter))
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(translatable("naturally_trimmed.config.trimTrades.minLevel"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.trimTrades.minLevel.desc")))
                                .binding(CONFIG_MANAGER.defaults().trimTrades.minLevel,
                                        () -> CONFIG_MANAGER.instance().trimTrades.minLevel,
                                        minLevel -> CONFIG_MANAGER.instance().trimTrades.minLevel = minLevel)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(1, 5)
                                        .step(1))
                                .build())
                        .build())
                .build();
    }

    private static OptionGroup buildTrimMobsGroup() {
        return OptionGroup.createBuilder()
                .name(translatable("naturally_trimmed.config.trimMobs"))
                .description(OptionDescription.of(translatable("naturally_trimmed.config.trimMobs.desc")))
                .collapsed(true)

                .option(Option.<Config.TrimMobsSubConfig.TrimSystem>createBuilder()
                        .name(translatable("naturally_trimmed.config.trimMobs.trimSystem"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.trimMobs.trimSystem.desc")))
                        .binding(CONFIG_MANAGER.defaults().trimMobs.trimSystem,
                                () -> CONFIG_MANAGER.instance().trimMobs.trimSystem,
                                enabledSystem -> CONFIG_MANAGER.instance().trimMobs.trimSystem = enabledSystem)
                        .controller(opt -> EnumControllerBuilder.create(opt)
                                .enumClass(Config.TrimMobsSubConfig.TrimSystem.class)
                                .formatValue(trimSystemFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(translatable("naturally_trimmed.config.trimMobs.noTrimsChance"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.trimMobs.noTrimsChance.desc")))
                        .binding(CONFIG_MANAGER.defaults().trimMobs.noTrimsChance,
                                () -> CONFIG_MANAGER.instance().trimMobs.noTrimsChance,
                                noTrimsChance -> CONFIG_MANAGER.instance().trimMobs.noTrimsChance = noTrimsChance)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(percentageFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(translatable("naturally_trimmed.config.trimMobs.trimChance"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.trimMobs.trimChance.desc")))
                        .binding(CONFIG_MANAGER.defaults().trimMobs.trimChance,
                                () -> CONFIG_MANAGER.instance().trimMobs.trimChance,
                                trimsChance -> CONFIG_MANAGER.instance().trimMobs.trimChance = trimsChance)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(percentageFormatter))
                        .build())
                .build();
    }

    private static ConfigCategory buildFilteringCategory() {
        return ConfigCategory.createBuilder()
                .name(translatable("naturally_trimmed.config.filtering"))
                .tooltip(translatable("naturally_trimmed.config.filtering.desc"))

                .option(Option.<Boolean>createBuilder()
                        .name(translatable("naturally_trimmed.config.filtering.textureValidationFiltering"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.filtering.textureValidationFiltering.desc")))
                        .binding(CONFIG_MANAGER.defaults().trimFiltering.textureValidationFiltering,
                                () -> CONFIG_MANAGER.instance().trimFiltering.textureValidationFiltering,
                                textureValidationFiltering -> CONFIG_MANAGER.instance().trimFiltering.textureValidationFiltering = textureValidationFiltering)
                        .controller(BooleanControllerBuilder::create)
                        .build())

                .option(Option.<Boolean>createBuilder()
                        .name(translatable("naturally_trimmed.config.filtering.vanillaOnly"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.filtering.vanillaOnly.desc")))
                        .binding(CONFIG_MANAGER.defaults().trimFiltering.vanillaOnly,
                                () -> CONFIG_MANAGER.instance().trimFiltering.vanillaOnly,
                                vanillaOnly -> CONFIG_MANAGER.instance().trimFiltering.vanillaOnly = vanillaOnly)
                        .controller(BooleanControllerBuilder::create)
                        .build())

                .group(ListOption.<String>createBuilder()
                        .name(translatable("naturally_trimmed.config.filtering.materialFilter"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.filtering.materialFilter.desc")))
                        .collapsed(true)
                        .binding(CONFIG_MANAGER.defaults().trimFiltering.materialFilter.stream().map(FilterRule::toString).toList(),
                                () -> CONFIG_MANAGER.instance().trimFiltering.materialFilter.stream().map(FilterRule::toString).toList(),
                                materialFilters -> CONFIG_MANAGER.instance().trimFiltering.materialFilter = materialFilters.stream().map(filter -> FilterRule.<TrimMaterial>construct(filter, true)).toList())
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())

                .group(ListOption.<String>createBuilder()
                        .name(translatable("naturally_trimmed.config.filtering.patternFilter"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.filtering.patternFilter.desc")))
                        .collapsed(true)
                        .binding(CONFIG_MANAGER.defaults().trimFiltering.patternFilter.stream().map(FilterRule::toString).toList(),
                                () -> CONFIG_MANAGER.instance().trimFiltering.patternFilter.stream().map(FilterRule::toString).toList(),
                                patternFilters -> CONFIG_MANAGER.instance().trimFiltering.patternFilter = patternFilters.stream().map(pattern -> FilterRule.<TrimPattern>construct(pattern, false)).toList())
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())
                .build();
    }

    private static ConfigCategory buildUtilsCategory() {
        boolean isInWorld = Minecraft.getInstance().level != null;

        return ConfigCategory.createBuilder()
                .name(translatable("naturally_trimmed.config.utils"))
                .tooltip(translatable("naturally_trimmed.config.utils.desc"))

                .option(ButtonOption.createBuilder()
                        .name((translatable("naturally_trimmed.config.utils.openFile")))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.utils.openFile")))
                        .text(translatable("naturally_trimmed.config.utils.run"))
                        .action((screen, option) -> Util.getPlatform().openPath(NaturallyTrimmed.getConfigPath()))
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(translatable("naturally_trimmed.config.utils.reloadConfig"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.utils.reloadConfig.desc")))
                        .text(translatable("naturally_trimmed.config.utils.run"))
                        .action((screen, option) -> {
                            CONFIG_MANAGER.loadInstance();
                            screen.onClose();
                        })
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(translatable("naturally_trimmed.config.utils.validatePredefinedTrims"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.utils.validatePredefinedTrims.desc")))
                        .text(isInWorld ? translatable("naturally_trimmed.config.utils.run") : translatable("naturally_trimmed.config.utils.run").withStyle(ChatFormatting.STRIKETHROUGH))
                        .action((screen, option) -> validatePredefinedTrims())
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(translatable("naturally_trimmed.config.utils.validateBlacklists"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.utils.validateBlacklists.desc")))
                        .text(isInWorld ? translatable("naturally_trimmed.config.utils.run") : translatable("naturally_trimmed.config.utils.run").withStyle(ChatFormatting.STRIKETHROUGH))
                        .action((screen, option) -> validateFilters())
                        .build())
                .build();
    }

    public static void validatePredefinedTrims() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || player == null) return;
        RegistryAccess registryAccess = level.registryAccess();

        message(player, literal("\nValidating predefined trims...\n"));

        int total = CONFIG_MANAGER.instance().trimMobs.predefinedTrims.size();
        int valid = 0;
        int index = 0;

        for (TrimData trimData : CONFIG_MANAGER.instance().trimMobs.predefinedTrims) {
            try {
                trimData.getTrim(registryAccess);
                valid += 1;
            } catch (NoSuchElementException | IdentifierException ignored) {
                message(player, literal("Found invalid trim: \"" + trimData + "\" with index " + index));
            } finally {
                index++;
            }
        }

        message(player, literal("\n" + valid + " out of " + total + " trims are valid."));
        message(player, literal("Done validating predefined trims"));
    }

    public static void validateFilters() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || player == null) return;
        RegistryAccess registryAccess = level.registryAccess();

        message(player, literal("\nValidating material filter:\n").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.BOLD));
        validateFilterList(TrimApplier.getTrimMaterials(registryAccess).stream().toList(), CONFIG_MANAGER.instance().trimFiltering.materialFilter, player);
        message(player, literal("\nDone validating material filter").withStyle(ChatFormatting.UNDERLINE));

        message(player, literal("\nValidating pattern filter:\n").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.BOLD));
        validateFilterList(TrimApplier.getTrimPatterns(registryAccess).stream().toList(), CONFIG_MANAGER.instance().trimFiltering.patternFilter, player);
        message(player, literal("\nDone validating pattern filter").withStyle(ChatFormatting.UNDERLINE));

        if (CONFIG_MANAGER.instance().trimFiltering.vanillaOnly) {
            message(player, literal("\nNote that all non-vanilla trims are blacklisted through the vanillaOnly config entry"));
        }
    }

    private static <T> void validateFilterList(List<Holder.Reference<T>> trimParts, List<FilterRule<T>> filter, LocalPlayer player) {
        // Trim Part, Filter that applies the trim part
        Map<Holder.Reference<T>, Optional<FilterRule<T>>> trimStates = new HashMap<>();
        List<FilterRule<T>> unnecessaryRules = new ArrayList<>(filter);

        for (Holder.Reference<T> trimPart : trimParts) {
            Optional<FilterRule<T>> relevantRule = Optional.empty();
            for (FilterRule<T> rule : filter) {
                if (rule.matches(trimPart) != FilterRule.FilterResult.Irrelevant) {
                    unnecessaryRules.remove(rule);
                    relevantRule = Optional.of(rule);
                    break;
                }
            }

            trimStates.put(trimPart, relevantRule);
        }

        message(player, literal("Trim Part -> State -> Rule").withStyle(ChatFormatting.UNDERLINE));
        for (Map.Entry<Holder.Reference<T>, Optional<FilterRule<T>>> state : trimStates.entrySet()) {
            String filteredState = "Whitelisted";
            String ruleString = "whitelisted by default";

            if (state.getValue().isPresent()) {
                FilterRule<T> rule = state.getValue().get();
                filteredState = rule.filterDirection.toString() + "ed";
                ruleString = rule.toString();
            }

            message(player, literal(state.getKey().key().identifier() + " -> " + filteredState + " -> " + ruleString));
        }

        message(player, literal("\nChecking for unused rules...").withStyle(ChatFormatting.UNDERLINE));
        for (FilterRule<T> rule : unnecessaryRules) {
            message(player, literal(rule.toString()));
        }
        message(player, literal("Done checking for unused rules").withStyle(ChatFormatting.UNDERLINE));
    }

    public static class Formatters {
        private Formatters() {
        }

        public static class Percentage implements ValueFormatter<Integer> {
            @Override
            public Component format(Integer value) {
                return literal(value + "%");
            }
        }

        public static class TrimSystem implements ValueFormatter<Config.TrimMobsSubConfig.TrimSystem> {
            @Override
            public Component format(Config.TrimMobsSubConfig.TrimSystem selectedSystem) {
                return switch (selectedSystem) {
                    case RANDOM_TRIMS -> translatable("naturally_trimmed.config.trimMobs.trimSystem.randomTrims");
                    case PREDEFINED_TRIMS ->
                            translatable("naturally_trimmed.config.trimMobs.trimSystem.predefinedTrims");
                };
            }
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