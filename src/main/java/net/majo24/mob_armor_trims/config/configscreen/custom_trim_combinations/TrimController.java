package net.majo24.mob_armor_trims.config.configscreen.custom_trim_combinations;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.majo24.mob_armor_trims.config.CustomTrim;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TrimController implements Controller<CustomTrim> {
    private final Option<CustomTrim> option;
    private final Controller<String> materialController;
    private final Controller<String> patternController;

    public TrimController(Option<CustomTrim> option, Function<Option<String>, ControllerBuilder<String>> materialController, Function<Option<String>, ControllerBuilder<String>> patternController) {
        this.option = option;
        this.materialController = createOption("Material:", materialController,
                option.pendingValue()::material,
                material -> option.requestSet(new CustomTrim(material, option.pendingValue().pattern()))).controller();
        this.patternController = createOption("Pattern:", patternController,
                option.pendingValue()::pattern,
                pattern -> option.requestSet(new CustomTrim(option.pendingValue().material(), pattern))).controller();
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

    /**
     * Gets the dedicated {@link Option} for this controller
     */
    @Override
    public Option<CustomTrim> option() {
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
        Dimension<Integer> materialDimension = widgetDimension.withWidth(widgetDimension.width() / 2);
        Dimension<Integer> patternDimension = materialDimension.withX(widgetDimension.x() + widgetDimension.width() / 2);

        return new ControllerElement(this, screen, widgetDimension,
                materialController.provideWidget(screen, materialDimension),
                patternController.provideWidget(screen, patternDimension));
    }

    public static class Builder implements ControllerBuilder<CustomTrim> {
        private final Option<CustomTrim> option;
        private final Function<Option<String>, ControllerBuilder<String>> materialController;
        private final Function<Option<String>, ControllerBuilder<String>> patternController;

        public Builder(Option<CustomTrim> option) {
            this.option = option;
            this.materialController = StringControllerBuilder::create;
            this.patternController = StringControllerBuilder::create;
        }

        public static Builder create(Option<CustomTrim> option) {
            return new Builder(option);
        }

        @Override
        public Controller<CustomTrim> build() {
            return new TrimController(option, materialController, patternController);
        }
    }

    public static class ControllerElement extends ControllerWidget<TrimController> {
        private final AbstractWidget materialWidget;
        private final AbstractWidget patternWidget;

        public ControllerElement(TrimController control, YACLScreen screen, Dimension<Integer> dim, AbstractWidget materialWidget, AbstractWidget patternWidget) {
            super(control, screen, dim);
            this.materialWidget = materialWidget;
            this.patternWidget = patternWidget;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            materialWidget.render(graphics, mouseX, mouseY, delta);
            patternWidget.render(graphics, mouseX, mouseY, delta);
        }

        @Override
        public void setDimension(Dimension<Integer> widgetDimension) {
            Dimension<Integer> materialDimension = widgetDimension.withWidth(widgetDimension.width() / 2);
            materialWidget.setDimension(materialDimension);
            patternWidget.setDimension(materialDimension.withX(widgetDimension.x() + widgetDimension.width() / 2));
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            materialWidget.mouseMoved(mouseX, mouseY);
            patternWidget.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean material = materialWidget.mouseClicked(mouseX, mouseY, button);
            boolean pattern = patternWidget.mouseClicked(mouseX, mouseY, button);
            return material || pattern;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            boolean material = materialWidget.mouseReleased(mouseX, mouseY, button);
            boolean pattern = patternWidget.mouseReleased(mouseX, mouseY, button);
            return  material || pattern;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            boolean material = materialWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            boolean pattern = patternWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            return material || pattern;
        }

        /*? <=1.20.1 {*/
        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
            boolean material = materialWidget.mouseScrolled(mouseX, mouseY, delta);
            boolean pattern = patternWidget.mouseScrolled(mouseX, mouseY, delta);
            return material || pattern;
        }
        /*?} else {*/
        /*@Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            boolean material = materialElement.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            boolean pattern = patternElement.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            return material || pattern;
        }
        *//*?}*/

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            boolean key = materialWidget.keyPressed(keyCode, scanCode, modifiers);
            boolean pattern = patternWidget.keyPressed(keyCode, scanCode, modifiers);
            return key || pattern;
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            boolean key = materialWidget.keyReleased(keyCode, scanCode, modifiers);
            boolean pattern = patternWidget.keyReleased(keyCode, scanCode, modifiers);
            return key || pattern;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            boolean material = materialWidget.charTyped(chr, modifiers);
            boolean pattern = patternWidget.charTyped(chr, modifiers);
            return material || pattern;
        }

        @Override
        public void setFocused(boolean focused) {
            materialWidget.setFocused(focused);
            patternWidget.setFocused(focused);
        }

        @Override
        public boolean isFocused() {
            boolean material = materialWidget.isFocused();
            boolean pattern = patternWidget.isFocused();
            return material ||pattern;
        }

        @Override
        public void unfocus() {
            materialWidget.unfocus();
            patternWidget.unfocus();
            super.unfocus();
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        public NarrationPriority narrationPriority() {
            return materialWidget.narrationPriority();
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {
            materialWidget.updateNarration(narrationElementOutput);
            patternWidget.updateNarration(narrationElementOutput);
        }
    }
}
