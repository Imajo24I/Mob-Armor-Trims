package net.majo24.mob_armor_trims.config.screen.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.majo24.mob_armor_trims.config.screen.controllers.helpers.ControllerHelper;
import net.majo24.mob_armor_trims.config.screen.controllers.helpers.ControllerWidgetHelper;
import net.majo24.mob_armor_trims.trim_combinations_system.CustomTrim;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class TrimController extends ControllerHelper<CustomTrim> {
    private final Controller<String> materialController;
    private final Controller<String> patternController;

    public TrimController(Option<CustomTrim> option, Function<Option<String>, ControllerBuilder<String>> materialController, Function<Option<String>, ControllerBuilder<String>> patternController) {
        super(option);

        this.materialController = createOption("Material:", materialController,
                option.pendingValue()::material,
                material -> option.requestSet(new CustomTrim(material, option.pendingValue().pattern()))).controller();
        this.patternController = createOption("Pattern:", patternController,
                option.pendingValue()::pattern,
                pattern -> option.requestSet(new CustomTrim(option.pendingValue().material(), pattern))).controller();
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

    public static class ControllerElement extends ControllerWidgetHelper<TrimController> {
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
        public List<GuiEventListener> guiEventsListeners() {
            return Arrays.asList(
                    materialWidget,
                    patternWidget
            );
        }
    }
}
