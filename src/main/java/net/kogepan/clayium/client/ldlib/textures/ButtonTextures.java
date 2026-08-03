package net.kogepan.clayium.client.ldlib.textures;

import net.kogepan.clayium.Clayium;

import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;

public class ButtonTextures {

    private static final ResourceLocation SPRITE = Clayium.id("textures/gui/buttons.png");

    public static final ButtonTexture CE_BUTTON = new ButtonTexture(0);
    public static final ButtonTexture RUN_ONCE = new ButtonTexture(1);
    public static final ButtonTexture STOP = new ButtonTexture(2);
    public static final ButtonTexture TARGET_DISPLAY = new ButtonTexture(3);
    public static final ButtonTexture CONTINUOUS = new ButtonTexture(4);
    public static final ButtonTexture TARGET_BLOCK = new ButtonTexture(5);
    public static final ButtonTexture TARGET_ENTITY = new ButtonTexture(6);
    public static final ButtonTexture TARGET_BOTH = new ButtonTexture(7);
    public static final ButtonTexture FIXED_TARGET = new ButtonTexture(8);
    public static final ButtonTexture RAYTRACE = new ButtonTexture(9);
    public static final ButtonTexture SNEAK_OFF = new ButtonTexture(10);
    public static final ButtonTexture SNEAK_ON = new ButtonTexture(11);

    public static class ButtonTexture {

        public final IGuiTexture disabled;
        public final IGuiTexture base;
        public final IGuiTexture hovered;

        ButtonTexture(int index) {
            this.disabled = SpriteTexture.of(SPRITE).setSprite(16 * index, 0, 16, 16);
            this.base = SpriteTexture.of(SPRITE).setSprite(16 * index, 16, 16, 16);
            this.hovered = SpriteTexture.of(SPRITE).setSprite(16 * index, 32, 16, 16);
        }
    }
}
