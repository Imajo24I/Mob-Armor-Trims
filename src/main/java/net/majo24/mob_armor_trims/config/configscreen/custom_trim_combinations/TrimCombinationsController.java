package net.majo24.mob_armor_trims.config.configscreen.custom_trim_combinations;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.LowProfileButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.majo24.mob_armor_trims.TrimApplier;
import net.majo24.mob_armor_trims.config.custom_trim_combinations.CustomTrim;
import net.majo24.mob_armor_trims.config.custom_trim_combinations.TrimCombination;
import net.majo24.mob_armor_trims.mixin.yacl.CategoryTabOptionListAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TrimCombinationsController implements Controller<TrimCombination> {
    private final Option<TrimCombination> option;
    private final Controller<String> applyOnController;
    private final Controller<CustomTrim> bootsTrimController;
    private final Controller<CustomTrim> leggingsTrimController;
    private final Controller<CustomTrim> chestplateTrimController;
    private final Controller<CustomTrim> helmetTrimController;
    private boolean collapsed;

    public TrimCombinationsController(Option<TrimCombination> option, Function<Option<String>, ControllerBuilder<String>> applyOnController, Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> bootsTrimController, Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> leggingsTrimController, Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> chestplateTrimController, Function<Option<CustomTrim>, ControllerBuilder<CustomTrim>> helmetTrimController) {
        this.option = option;
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

    private static <T> Option<T> createOption(String name, Function<Option<T>, ControllerBuilder<T>> controller, Supplier<T> get, Consumer<T> set) {
        return Option.<T>createBuilder()
                .name(Component.literal(name))
                .binding(
                        get.get(),
                        get,
                        set
                )
                .instant(true)
                .controller(controller)
                .build();
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
    }

    /**
     * Gets the dedicated {@link Option} for this controller
     */
    @Override
    public Option<TrimCombination> option() {
        return option;
    }

    /**
     * Gets the formatted value based on {@link Option#pendingValue()}
     */
    @Override
    public Component formatValue() {
        return Component.empty();
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

    public static class ControllerElement extends ControllerWidget<TrimCombinationsController> {
        private final LowProfileButtonWidget collapseWidget;
        private final AbstractWidget applyOnMaterialWidget;
        private final AbstractWidget bootsTrimWidget;
        private final AbstractWidget leggingsTrimWidget;
        private final AbstractWidget chestplateTrimWidget;
        private final AbstractWidget helmetTrimWidget;

        public static final List<ItemStack> DIAMOND_ARMOR_ITEMS = List.of(
                Items.DIAMOND_BOOTS.getDefaultInstance(),
                Items.DIAMOND_LEGGINGS.getDefaultInstance(),
                Items.DIAMOND_CHESTPLATE.getDefaultInstance(),
                Items.DIAMOND_HELMET.getDefaultInstance()
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
                            ((CategoryTabOptionListAccessor) categoryTab).getOptionList().getList().refreshOptions();
                        }
                    });
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            collapseWidget.render(graphics, mouseX, mouseY, delta);

            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;

            renderMaterialToApplyOnPreview(graphics, mc);
            renderArmorPreviews(graphics, mc, level);

            if (!control.collapsed) {
                applyOnMaterialWidget.render(graphics, mouseX, mouseY, delta);
                bootsTrimWidget.render(graphics, mouseX, mouseY, delta);
                leggingsTrimWidget.render(graphics, mouseX, mouseY, delta);
                chestplateTrimWidget.render(graphics, mouseX, mouseY, delta);
                helmetTrimWidget.render(graphics, mouseX, mouseY, delta);
            }
        }

        private void renderMaterialToApplyOnPreview(GuiGraphics graphics, Minecraft mc) {
            String materialToApplyOn = control.option.pendingValue().materialToApplyTo();
            ItemStack armorItem = switch (materialToApplyOn) {
                case "netherite" -> Items.NETHERITE_CHESTPLATE.getDefaultInstance();
                case "diamond" -> Items.DIAMOND_CHESTPLATE.getDefaultInstance();
                case "gold" -> Items.GOLDEN_CHESTPLATE.getDefaultInstance();
                case "iron" -> Items.IRON_CHESTPLATE.getDefaultInstance();
                case "chain" -> Items.CHAINMAIL_CHESTPLATE.getDefaultInstance();
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

            if (armorItem != null) {
                graphics.renderItem(armorItem, x, y);
            } else {
                graphics.renderItem(Items.DIAMOND_CHESTPLATE.getDefaultInstance(), x, y);
                graphics.drawCenteredString(mc.font, "!", x, y, -65536);
            }
        }

        private void renderArmorPreviews(GuiGraphics graphics, Minecraft mc, ClientLevel level) {
            List<CustomTrim> customTrims = control.option.pendingValue().trims();

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
                ItemStack armor = DIAMOND_ARMOR_ITEMS.get(index);
                boolean validTrim = true;

                if (level != null) {
                    // As RegistryAccess is accessible, apply trim to armor preview and render exclamation mark if not valid
                    RegistryAccess registryAccess = level.registryAccess();
                    try {
                        ArmorTrim armorTrim = customTrims.get(index).getTrim(registryAccess);
                        TrimApplier.applyTrim(armor, armorTrim, registryAccess);
                    } catch (IllegalStateException e) {
                        validTrim = false;
                    }

                    graphics.renderItem(armor, previewX, previewY);

                    if (!validTrim) {
                        // Armor Trim is not valid, so draw an exclamation mark to indicate this
                        graphics.drawCenteredString(mc.font, "!", previewX, previewY, -65536);
                    }
                } else {
                    // RegistryAccess is not accessible, so draw a question mark over it,
                    // indicating preview cannot be correctly displayed
                    graphics.renderItem(armor, previewX, previewY);
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
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
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
            bootsTrimWidget.setDimension(defaultWidgetDimensions.moved(0, 40));
            leggingsTrimWidget.setDimension(defaultWidgetDimensions.moved(0, 60));
            chestplateTrimWidget.setDimension(defaultWidgetDimensions.moved(0, 80));
            helmetTrimWidget.setDimension(defaultWidgetDimensions.moved(0, 100));

            super.setDimension(widgetDimension);
        }

        @Override
        public void unfocus() {
            super.unfocus();

            collapseWidget.setFocused(false);
            applyOnMaterialWidget.setFocused(false);
            bootsTrimWidget.setFocused(false);
            leggingsTrimWidget.setFocused(false);
            chestplateTrimWidget.setFocused(false);
            helmetTrimWidget.setFocused(false);
        }

        @Override
        public boolean isFocused() {
            return collapseWidget.isFocused() || applyOnMaterialWidget.isFocused() || bootsTrimWidget.isFocused() || leggingsTrimWidget.isFocused() || chestplateTrimWidget.isFocused() || helmetTrimWidget.isFocused();
        }

        @Override
        public void setFocused(boolean focused) {
            collapseWidget.setFocused(focused);
            applyOnMaterialWidget.setFocused(focused);
            bootsTrimWidget.setFocused(focused);
            leggingsTrimWidget.setFocused(focused);
            chestplateTrimWidget.setFocused(focused);
            helmetTrimWidget.setFocused(focused);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return collapseWidget.charTyped(chr, modifiers)
                    || applyOnMaterialWidget.charTyped(chr, modifiers)
                    || bootsTrimWidget.charTyped(chr, modifiers)
                    || leggingsTrimWidget.charTyped(chr, modifiers)
                    || chestplateTrimWidget.charTyped(chr, modifiers)
                    || helmetTrimWidget.charTyped(chr, modifiers);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return collapseWidget.keyPressed(keyCode, scanCode, modifiers) || applyOnMaterialWidget.keyPressed(keyCode, scanCode, modifiers) || bootsTrimWidget.keyPressed(keyCode, scanCode, modifiers) || leggingsTrimWidget.keyPressed(keyCode, scanCode, modifiers) || chestplateTrimWidget.keyPressed(keyCode, scanCode, modifiers) || helmetTrimWidget.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return collapseWidget.keyReleased(keyCode, scanCode, modifiers) || applyOnMaterialWidget.keyReleased(keyCode, scanCode, modifiers) || bootsTrimWidget.keyReleased(keyCode, scanCode, modifiers) || leggingsTrimWidget.keyReleased(keyCode, scanCode, modifiers) || chestplateTrimWidget.keyReleased(keyCode, scanCode, modifiers) || helmetTrimWidget.keyReleased(keyCode, scanCode, modifiers);
        }

        /*? <=1.20.1 {*/
        /*@Override
        public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
            return collapseWidget.mouseScrolled(mouseX, mouseY, delta) || applyOnMaterialWidget.mouseScrolled(mouseX, mouseY, delta) || bootsTrimWidget.mouseScrolled(mouseX, mouseY, delta) || leggingsTrimWidget.mouseScrolled(mouseX, mouseY, delta) || chestplateTrimWidget.mouseScrolled(mouseX, mouseY, delta) || helmetTrimWidget.mouseScrolled(mouseX, mouseY, delta);
        }
        *//*?} else {*/
        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            return collapseWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY) || applyOnMaterialWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY) || bootsTrimWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY) || leggingsTrimWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY) || chestplateTrimWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY) || helmetTrimWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        /*?}*/

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            collapseWidget.mouseMoved(mouseX, mouseY);
            applyOnMaterialWidget.mouseMoved(mouseX, mouseY);
            bootsTrimWidget.mouseMoved(mouseX, mouseY);
            leggingsTrimWidget.mouseMoved(mouseX, mouseY);
            chestplateTrimWidget.mouseMoved(mouseX, mouseY);
            helmetTrimWidget.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return collapseWidget.mouseClicked(mouseX, mouseY, button) || applyOnMaterialWidget.mouseClicked(mouseX, mouseY, button) || bootsTrimWidget.mouseClicked(mouseX, mouseY, button) || leggingsTrimWidget.mouseClicked(mouseX, mouseY, button) || chestplateTrimWidget.mouseClicked(mouseX, mouseY, button) || helmetTrimWidget.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return collapseWidget.mouseReleased(mouseX, mouseY, button) || applyOnMaterialWidget.mouseReleased(mouseX, mouseY, button) || bootsTrimWidget.mouseReleased(mouseX, mouseY, button) || leggingsTrimWidget.mouseReleased(mouseX, mouseY, button) || chestplateTrimWidget.mouseReleased(mouseX, mouseY, button) || helmetTrimWidget.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return collapseWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || applyOnMaterialWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || bootsTrimWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || leggingsTrimWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || chestplateTrimWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || helmetTrimWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public NarrationPriority narrationPriority() {
            return collapseWidget.narrationPriority();
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {
            collapseWidget.updateNarration(narrationElementOutput);
            applyOnMaterialWidget.updateNarration(narrationElementOutput);
            bootsTrimWidget.updateNarration(narrationElementOutput);
            leggingsTrimWidget.updateNarration(narrationElementOutput);
            chestplateTrimWidget.updateNarration(narrationElementOutput);
            helmetTrimWidget.updateNarration(narrationElementOutput);
        }
    }
}
