package net.kogepan.clayium.client.ldlib.textures;

import net.kogepan.clayium.Clayium;

import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;

public class XEITextures {

    private static final ResourceLocation BADGE_SPRITE = Clayium.id("textures/gui/xei_badge.png");
    private static final ResourceLocation SLOT_SPRITE = Clayium.id("textures/gui/xei_slot.png");

    public static final IGuiTexture BADGE = SpriteTexture.of(BADGE_SPRITE).setSprite(0, 0, 28, 11);
    public static final IGuiTexture SLOT = SpriteTexture.of(SLOT_SPRITE).setSprite(0, 0, 69, 18);
}
