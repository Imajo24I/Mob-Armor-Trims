package net.majo24.mob_armor_trims.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
  protected ConfigScreen() {
    // The parameter is the title of the screen,
    // which will be narrated when you enter the screen.
    super(Text.literal("My tutorial screen"));
  }


  public ButtonWidget button1;
  public SliderWidget trimChancesSlider;

  @Override
  protected void init() {
    button1 = ButtonWidget.builder(Text.literal("Button 1"), button -> {
      System.out.println("You clicked button1!");
    })
        .dimensions(width / 2 - 205, 20, 200, 20)
        .tooltip(Tooltip.of(Text.literal("Tooltip of button1")))
        .build();


    addDrawableChild(button1);
  }
}
