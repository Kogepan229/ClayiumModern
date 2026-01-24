package net.kogepan.clayium.client.ldlib.textures;

import net.kogepan.clayium.Clayium;

import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;

public class SlotTextures {

    private static final ResourceLocation SPRITE = Clayium.id("textures/gui/slots.png");

    public static final IGuiTexture INPUT_SLOT_1 = SpriteTexture.of(SPRITE).setSprite(0, 0, 18, 18);
    public static final IGuiTexture INPUT_SLOT_2 = SpriteTexture.of(SPRITE).setSprite(18, 0, 18, 18);
    public static final IGuiTexture INPUT_SLOT_3 = SpriteTexture.of(SPRITE).setSprite(36, 0, 18, 18);
    public static final IGuiTexture OUTPUT_SLOT_1 = SpriteTexture.of(SPRITE).setSprite(0, 18, 18, 18);
    public static final IGuiTexture OUTPUT_SLOT_2 = SpriteTexture.of(SPRITE).setSprite(18, 18, 18, 18);
    public static final IGuiTexture OUTPUT_SLOT_3 = SpriteTexture.of(SPRITE).setSprite(36, 18, 18, 18);
}
