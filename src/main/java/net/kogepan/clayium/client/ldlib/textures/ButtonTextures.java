package net.kogepan.clayium.client.ldlib.textures;

import net.kogepan.clayium.Clayium;

import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;

public class ButtonTextures {

    private static final ResourceLocation SPRITE = Clayium.id("textures/gui/buttons.png");

    public static final ButtonTexture CE_BUTTON = new ButtonTexture(0);

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
