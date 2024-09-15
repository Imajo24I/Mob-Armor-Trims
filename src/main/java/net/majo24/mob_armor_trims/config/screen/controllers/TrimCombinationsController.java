package net.majo24.mob_armor_trims.config.screen.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.LowProfileButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.majo24.mob_armor_trims.TrimApplier;
import net.majo24.mob_armor_trims.config.screen.controllers.helpers.ControllerHelper;
import net.majo24.mob_armor_trims.config.screen.controllers.helpers.ControllerWidgetHelper;
import net.majo24.mob_armor_trims.trim_combinations_system.CustomTrim;
import net.majo24.mob_armor_trims.trim_combinations_system.TrimCombination;
import net.majo24.mob_armor_trims.mixin.yacl.OptionListAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TrimCombinationsController extends ControllerHelper<TrimCombination> {
    private final Controller<String> applyOnController;
    private final Controller<CustomTrim> bootsTrimController;
    private final Controller<CustomTrim> leggingsTrimController;
    private final Controller<CustomTrim> chestplateTrimController;
    private final Controller<CustomTrim> helmetTrimController;
    private boolean collapsed;

    public TrimCombinationsController(Option<TrimCombination> option, Function<Option<String>, ControllerBuilder<String>> applyOnController, Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> bootsTrimController, Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> leggingsTrimController, Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> chestplateTrimController, Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> helmetTrimController) {
        super(option);

        this.collapsed = true;

        this.bootsTrimController = createOption("Boots Trim:", bootsTrimController,
                () -> option.pendingValue().bootsTrim(),
                value -> {
                    TrimCombination trimCombination = option.pendingValue();
                    option.requestSet(new TrimCombination(trimCombination.materialToApplyTo(), trimCombination.helmetTrim(), trimCombination.chestplateTrim(), trimCombination.leggingsTrim(), value));
                }).controller();

        this.leggingsTrimController = createOption("Leggings Trim:", leggingsTrimController,
                () -> option.pendingValue().leggingsTrim(),
                value -> {
                    TrimCombination trimCombination = option.pendingValue();
                    option.requestSet(new TrimCombination(trimCombination.materialToApplyTo(), trimCombination.helmetTrim(), trimCombination.chestplateTrim(), value, trimCombination.bootsTrim()));
                }).controller();

        this.chestplateTrimController = createOption("Chestplate Trim:", chestplateTrimController,
                () -> option.pendingValue().chestplateTrim(),
                value -> {
                    TrimCombination trimCombination = option.pendingValue();
                    option.requestSet(new TrimCombination(trimCombination.materialToApplyTo(), trimCombination.helmetTrim(), value, trimCombination.leggingsTrim(), trimCombination.bootsTrim()));
                }).controller();

        this.helmetTrimController = createOption("Helmet Trim:", helmetTrimController,
                () -> option.pendingValue().helmetTrim(),
                value -> {
                    TrimCombination trimCombination = option.pendingValue();
                    option.requestSet(new TrimCombination(trimCombination.materialToApplyTo(), value, trimCombination.chestplateTrim(), trimCombination.leggingsTrim(), trimCombination.bootsTrim()));
                }).controller();

        this.applyOnController = createOption("Material to apply on:", applyOnController,
                () -> option.pendingValue().materialToApplyTo(),
                value -> {
                    TrimCombination trimCombination = option.pendingValue();
                    option.requestSet(new TrimCombination(value, trimCombination.helmetTrim(), trimCombination.chestplateTrim(), trimCombination.leggingsTrim(), trimCombination.bootsTrim()));
                }
        ).controller();
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
    }

    /**
     * Provides a widget to display
     *
     * @param screen          parent screen
     * @param widgetDimension dimensions of the widget
     */
    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        if (collapsed) {
            widgetDimension = widgetDimension.withHeight(20);
        } else {
            widgetDimension = widgetDimension.withHeight(120);
        }


        AbstractWidget applyOnMaterialWidget = this.applyOnController.provideWidget(screen, widgetDimension.moved(0, 20));
        AbstractWidget bootsTrimWidget = this.bootsTrimController.provideWidget(screen, widgetDimension.moved(0, 40));
        AbstractWidget leggingsTrimWidget = this.leggingsTrimController.provideWidget(screen, widgetDimension.moved(0, 60));
        AbstractWidget chestplateTrimWidget = this.chestplateTrimController.provideWidget(screen, widgetDimension.moved(0, 80));
        AbstractWidget helmetTrimWidget = this.helmetTrimController.provideWidget(screen, widgetDimension.moved(0, 100));

        return new ControllerElement(this, screen, widgetDimension, applyOnMaterialWidget, bootsTrimWidget, leggingsTrimWidget, chestplateTrimWidget, helmetTrimWidget);
    }

    public static class Builder implements ControllerBuilder<TrimCombination> {
        protected final Option<TrimCombination> option;
        private final Function<Option<String>, ControllerBuilder<String>> applyOnMaterialController;
        private final Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> bootsTrimController;
        private final Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> leggingsTrimController;
        private final Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> chestplateTrimController;
        private final Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> helmetTrimController;

        public Builder(Option<TrimCombination> option) {
            this.option = option;
            this.applyOnMaterialController = StringControllerBuilder::create;
            this.bootsTrimController = TrimController.Builder::create;
            this.leggingsTrimController = TrimController.Builder::create;
            this.chestplateTrimController = TrimController.Builder::create;
            this.helmetTrimController = TrimController.Builder::create;
        }

        public static Builder create(Option<TrimCombination> option) {
            return new Builder(option);
        }

        @Override
        public Controller<TrimCombination> build() {
            return new TrimCombinationsController(option, applyOnMaterialController, bootsTrimController, leggingsTrimController, chestplateTrimController, helmetTrimController);
        }
    }

    public static class ControllerElement extends ControllerWidgetHelper<TrimCombinationsController> {
        private final LowProfileButtonWidget collapseWidget;
        private final AbstractWidget applyOnMaterialWidget;
        private final AbstractWidget bootsTrimWidget;
        private final AbstractWidget leggingsTrimWidget;
        private final AbstractWidget chestplateTrimWidget;
        private final AbstractWidget helmetTrimWidget;

        public static final List<List<ItemStack>> ARMOR_ITEMS = List.of(
                List.of(
                        Items.LEATHER_BOOTS.getDefaultInstance(),
                        Items.LEATHER_LEGGINGS.getDefaultInstance(),
                        Items.LEATHER_CHESTPLATE.getDefaultInstance(),
                        Items.LEATHER_HELMET.getDefaultInstance()
                ),
                List.of(
                        Items.CHAINMAIL_BOOTS.getDefaultInstance(),
                        Items.CHAINMAIL_LEGGINGS.getDefaultInstance(),
                        Items.CHAINMAIL_CHESTPLATE.getDefaultInstance(),
                        Items.CHAINMAIL_HELMET.getDefaultInstance()
                ),
                List.of(
                        Items.IRON_BOOTS.getDefaultInstance(),
                        Items.IRON_LEGGINGS.getDefaultInstance(),
                        Items.IRON_CHESTPLATE.getDefaultInstance(),
                        Items.IRON_HELMET.getDefaultInstance()
                ),
                List.of(
                        Items.GOLDEN_BOOTS.getDefaultInstance(),
                        Items.GOLDEN_LEGGINGS.getDefaultInstance(),
                        Items.GOLDEN_CHESTPLATE.getDefaultInstance(),
                        Items.GOLDEN_HELMET.getDefaultInstance()
                ),
                List.of(
                        Items.DIAMOND_BOOTS.getDefaultInstance(),
                        Items.DIAMOND_LEGGINGS.getDefaultInstance(),
                        Items.DIAMOND_CHESTPLATE.getDefaultInstance(),
                        Items.DIAMOND_HELMET.getDefaultInstance()
                ),
                List.of(
                        Items.NETHERITE_BOOTS.getDefaultInstance(),
                        Items.NETHERITE_LEGGINGS.getDefaultInstance(),
                        Items.NETHERITE_CHESTPLATE.getDefaultInstance(),
                        Items.NETHERITE_HELMET.getDefaultInstance()
                )
        );

        public ControllerElement(TrimCombinationsController control, YACLScreen screen, Dimension<Integer> dim, AbstractWidget applyOnMaterialWidget, AbstractWidget bootsTrimWidget, AbstractWidget leggingsTrimWidget, AbstractWidget chestplateTrimWidget, AbstractWidget helmetTrimWidget) {
            super(control, screen, dim);

            this.applyOnMaterialWidget = applyOnMaterialWidget;
            this.bootsTrimWidget = bootsTrimWidget;
            this.leggingsTrimWidget = leggingsTrimWidget;
            this.chestplateTrimWidget = chestplateTrimWidget;
            this.helmetTrimWidget = helmetTrimWidget;

            this.collapseWidget = new LowProfileButtonWidget(dim.x(), dim.y(),
                    20, 20,
                    Component.literal(control.collapsed ? "▶" : "▼"),
                    buttonWidget -> {
                        control.setCollapsed(!control.collapsed);
                        buttonWidget.setMessage(Component.literal(control.collapsed ? "▶" : "▼"));

                        if (this.screen.tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab) {
                            ((OptionListAccessor) categoryTab).getOptionList().getList().refreshOptions();
                        }
                    });
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            collapseWidget.render(graphics, mouseX, mouseY, delta);

            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;

            renderMaterialToApplyOnPreview(graphics);
            renderArmorPreviews(graphics, mc, level);

            if (!control.collapsed) {
                applyOnMaterialWidget.render(graphics, mouseX, mouseY, delta);
                bootsTrimWidget.render(graphics, mouseX, mouseY, delta);
                leggingsTrimWidget.render(graphics, mouseX, mouseY, delta);
                chestplateTrimWidget.render(graphics, mouseX, mouseY, delta);
                helmetTrimWidget.render(graphics, mouseX, mouseY, delta);
            }
        }

        private void renderMaterialToApplyOnPreview(GuiGraphics graphics) {
            String materialToApplyOn = control.option().pendingValue().materialToApplyTo();
            ItemStack armorItem = switch (materialToApplyOn) {
                case "netherite" -> Items.NETHERITE_CHESTPLATE.getDefaultInstance();
                case "diamond" -> Items.DIAMOND_CHESTPLATE.getDefaultInstance();
                case "gold" -> Items.GOLDEN_CHESTPLATE.getDefaultInstance();
                case "iron" -> Items.IRON_CHESTPLATE.getDefaultInstance();
                case "chainmail" -> Items.CHAINMAIL_CHESTPLATE.getDefaultInstance();
                case "leather" -> Items.LEATHER_CHESTPLATE.getDefaultInstance();
                default -> null;
            };

            int x;
            int y;

            if (control.collapsed) {
                x = collapseWidget.getX() + 45;
                y = collapseWidget.getY() + 3;
            } else {
                x = collapseWidget.getX() - 37;
                y = collapseWidget.getY() + 23;
            }

            graphics.renderItem(
                    Objects.requireNonNullElseGet(armorItem, Items.BARRIER::getDefaultInstance)
                    , x, y);
        }

        private void renderArmorPreviews(GuiGraphics graphics, Minecraft mc, ClientLevel level) {
            List<CustomTrim> customTrims = control.option().pendingValue().trims();

            int previewX;
            int previewY;

            if (control.collapsed) {
                previewX = collapseWidget.getX() + 80;
                previewY = collapseWidget.getY() + 3;
            } else {
                previewX = collapseWidget.getX() - 36;
                previewY = collapseWidget.getY() + 43;
            }

            for (int index = 3; index >= 0; index--) {
                List<ItemStack> armorItems = switch (control.option().pendingValue().materialToApplyTo()) {
                    case "leather" -> ARMOR_ITEMS.get(0);
                    case "chainmail" -> ARMOR_ITEMS.get(1);
                    case "iron" -> ARMOR_ITEMS.get(2);
                    case "diamond" -> ARMOR_ITEMS.get(4);
                    case "netherite" -> ARMOR_ITEMS.get(5);
                    default -> ARMOR_ITEMS.get(3);
                };
                ItemStack preview = armorItems.get(index);

                if (level != null) {
                    // As RegistryAccess is accessible, apply trim to armor preview and render a barrier if not valid
                    RegistryAccess registryAccess = level.registryAccess();
                    try {
                        ArmorTrim armorTrim = customTrims.get(index).getTrim(registryAccess);
                        TrimApplier.applyTrim(preview, armorTrim, registryAccess);
                    } catch (IllegalStateException e) {
                        preview = Items.BARRIER.getDefaultInstance();
                    }

                    graphics.renderItem(preview, previewX, previewY);
                } else {
                    // RegistryAccess is not accessible, so draw a question mark over the preview,
                    // indicating it cannot be correctly displayed
                    graphics.renderItem(Items.BARRIER.getDefaultInstance(), previewX, previewY);
                    graphics.drawCenteredString(mc.font, "?", previewX, previewY, 0xffffff);
                }

                if (control.collapsed) {
                    previewX += 20;
                } else {
                    previewY += 20;
                }
            }
        }

        @Override
        public void setDimension(Dimension<Integer> widgetDimension) {
            Dimension<Integer> defaultWidgetDimensions;

            if (control.collapsed) {
                widgetDimension = widgetDimension.withHeight(20);
                defaultWidgetDimensions = widgetDimension;
            } else {
                widgetDimension.withHeight(120);
                defaultWidgetDimensions = widgetDimension.withHeight(20).withX(widgetDimension.x() - 20).withWidth(widgetDimension.width() + 40);
            }

            collapseWidget.setX(widgetDimension.x());
            collapseWidget.setY(widgetDimension.y());
            collapseWidget.setWidth(collapseWidget.getWidth());

            applyOnMaterialWidget.setDimension(defaultWidgetDimensions.moved(0, 20));
            helmetTrimWidget.setDimension(defaultWidgetDimensions.moved(0, 40));
            chestplateTrimWidget.setDimension(defaultWidgetDimensions.moved(0, 60));
            leggingsTrimWidget.setDimension(defaultWidgetDimensions.moved(0, 80));
            bootsTrimWidget.setDimension(defaultWidgetDimensions.moved(0, 100));

            super.setDimension(widgetDimension);
        }

        public List<GuiEventListener> guiEventsListeners() {
            return Arrays.asList(
                    collapseWidget,
                    applyOnMaterialWidget,
                    helmetTrimWidget,
                    chestplateTrimWidget,
                    leggingsTrimWidget,
                    bootsTrimWidget
            );
        }
    }
}
