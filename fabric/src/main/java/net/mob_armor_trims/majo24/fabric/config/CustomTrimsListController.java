package net.mob_armor_trims.majo24.fabric.config;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomTrimsListController implements Controller<List<String>> {
    private final Option<List<String>> option;
    private final Controller<String> materialController;
    private final Controller<String> patternController;

    public CustomTrimsListController(Option<List<String>> option, Function<Option<String>, ControllerBuilder<String>> materialController, Function<Option<String>, ControllerBuilder<String>> patternController) {
        this.option = option;

        this.materialController = dummyOption("Material:", materialController,
                () -> option.pendingValue().get(0),
                newMaterial -> option.requestSet(Arrays.asList(newMaterial, option.pendingValue().get(1)))
        ).controller();

        this.patternController = dummyOption("Pattern:", materialController,
                () -> option.pendingValue().get(1),
                newPattern -> option.requestSet(Arrays.asList(option.pendingValue().get(0), newPattern))
        ).controller();
    }

    private static Option<String> dummyOption(String name, Function<Option<String>, ControllerBuilder<String>> controller, Supplier<String> get, Consumer<String> set) {
        return Option.<String>createBuilder()
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
    public Option<List<String>> option() {
        return option;
    }

    /**
     * Gets the formatted value based on {@link Option#pendingValue()}
     */
    @Override
    public Component formatValue() {
        return materialController.formatValue().copy().append(" | ").append(patternController.formatValue());
    }

    /**
     * Provides a widget to display
     *
     * @param screen          parent screen
     * @param widgetDimension
     */
    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        Dimension<Integer> materialDimension = widgetDimension.withWidth((int)((double)widgetDimension.width() * 0.5));
        Dimension<Integer> patternDimension = widgetDimension.moved(materialDimension.width(), 0).withWidth((int)((double)widgetDimension.width() - materialDimension.width()));
        AbstractWidget materialWidget = materialController.provideWidget(screen, materialDimension);
        AbstractWidget patternWidget = patternController.provideWidget(screen, patternDimension);
        return new TwoFieldListControllerElement(this, screen, widgetDimension, materialWidget, patternWidget);
    }

    public static class Builder implements ControllerBuilder<List<String>> {
        protected final Option<List<String>> option;
        private Function<Option<String>, ControllerBuilder<String>> materialController;
        private Function<Option<String>, ControllerBuilder<String>> patternController;

        public Builder(Option<List<String>> option) {
            this.option = option;
        }

        static Builder create(Option<List<String>> option) {
            return new Builder(option);
        }

        public Builder materialController(Function<Option<String>, ControllerBuilder<String>> materialController) {
            this.materialController = materialController;
            return this;
        }

        public Builder patternController(Function<Option<String>, ControllerBuilder<String>> patternController) {
            this.patternController = patternController;
            return this;
        }

        @Override
        public Controller<List<String>> build() {
            return new CustomTrimsListController(option, materialController, patternController);
        }
    }

    public static class TwoFieldListControllerElement extends ControllerWidget<CustomTrimsListController> {
        private final AbstractWidget materialElement;
        private final AbstractWidget patternElement;

        public TwoFieldListControllerElement(CustomTrimsListController control, YACLScreen screen, Dimension<Integer> dim, AbstractWidget materialElement, AbstractWidget patternElement) {
            super(control, screen, dim);
            this.materialElement = materialElement;
            this.patternElement = patternElement;
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            materialElement.mouseMoved(mouseX, mouseY);
            patternElement.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean material = materialElement.mouseClicked(mouseX, mouseY, button);
            boolean pattern = patternElement.mouseClicked(mouseX, mouseY, button);
            return material || pattern;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            boolean material = materialElement.mouseReleased(mouseX, mouseY, button);
            boolean pattern = patternElement.mouseReleased(mouseX, mouseY, button);
            return  material || pattern;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            boolean material = materialElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            boolean pattern = patternElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            return material || pattern;
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            boolean material = materialElement.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            boolean pattern = patternElement.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            return material || pattern;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            boolean key = materialElement.keyPressed(keyCode, scanCode, modifiers);
            boolean pattern = patternElement.keyPressed(keyCode, scanCode, modifiers);
            return key || pattern;
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            boolean key = materialElement.keyReleased(keyCode, scanCode, modifiers);
            boolean pattern = patternElement.keyReleased(keyCode, scanCode, modifiers);
            return key || pattern;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            boolean material = materialElement.charTyped(chr, modifiers);
            boolean pattern = patternElement.charTyped(chr, modifiers);
            return material || pattern;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        public void setDimension(Dimension<Integer> dim) {
            Dimension<Integer> materialDimension = dim.withWidth((int)((double)dim.width() * 0.5));
            Dimension<Integer> patternDimension = dim.moved(materialDimension.width(), 0).withWidth((int)((double)dim.width() - materialDimension.width()));
            materialElement.setDimension(materialDimension);
            patternElement.setDimension(patternDimension);
            super.setDimension(dim);
        }

        @Override
        public void unfocus() {
            materialElement.unfocus();
            patternElement.unfocus();
            super.unfocus();
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            materialElement.render(graphics, mouseX, mouseY, delta);
            patternElement.render(graphics, mouseX, mouseY, delta);
        }
    }
}
