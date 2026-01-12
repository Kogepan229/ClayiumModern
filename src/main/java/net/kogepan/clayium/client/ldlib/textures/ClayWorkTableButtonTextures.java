package net.kogepan.clayium.client.ldlib.textures;

import net.kogepan.clayium.Clayium;

import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;

public class ClayWorkTableButtonTextures {

    public final static ButtonTexture BUTTON1 = new ButtonTexture(0);
    public final static ButtonTexture BUTTON2 = new ButtonTexture(1);
    public final static ButtonTexture BUTTON3 = new ButtonTexture(2);
    public final static ButtonTexture BUTTON4 = new ButtonTexture(3);
    public final static ButtonTexture BUTTON5 = new ButtonTexture(4);
    public final static ButtonTexture BUTTON6 = new ButtonTexture(5);

    public static class ButtonTexture {

        private static final ResourceLocation SPRITE = Clayium.id("textures/gui/clay_work_table_buttons.png");
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
