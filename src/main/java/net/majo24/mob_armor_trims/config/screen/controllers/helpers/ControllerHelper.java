package net.majo24.mob_armor_trims.config.screen.controllers.helpers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Helper class for {@link Controller}
 */
public abstract class ControllerHelper<T> implements Controller<T> {
    private final Option<T> option;

    protected ControllerHelper(Option<T> option) {
        this.option = option;
    }

    @Override
    public Option<T> option() {
        return option;
    }

    /**
     * Gets the formatted value based on {@link Option#pendingValue()}
     */
    @Override
    public Component formatValue() {
        return Component.empty();
    }

    public static <T> Option<T> createOption(String name, Function<Option<T>, ControllerBuilder<T>> controllerBuilder, Supplier<T> get, Consumer<T> set) {
        return Option.<T>createBuilder()
                .name(Component.literal(name))
                .binding(
                        get.get(),
                        get,
                        set
                )

                //TODO: Replace with stateManager
                // Currently not done, to maintain compatibility with YACL 3.5.2,
                // since YACL 3.5.4+ causes crashes on Forge 1.20.1
                // Once this bug has been fixed, this can be done
                // (Or just use Stonecutter versioning and don't have to wait)
                .instant(true)

                .controller(controllerBuilder)
                .build();
    }
}