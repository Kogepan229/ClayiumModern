package net.kogepan.clayium.client.ldlib.textures;

import net.kogepan.clayium.Clayium;

import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import org.jetbrains.annotations.NotNull;

public class SlotTextures {

    private static final ResourceLocation SPRITE = Clayium.id("textures/gui/slots.png");

    public static final IGuiTexture INPUT_SLOT_1 = SpriteTexture.of(SPRITE).setSprite(0, 0, 18, 18);
    public static final IGuiTexture INPUT_SLOT_2 = SpriteTexture.of(SPRITE).setSprite(18, 0, 18, 18);
    public static final IGuiTexture INPUT_SLOT_3 = SpriteTexture.of(SPRITE).setSprite(36, 0, 18, 18);
    public static final IGuiTexture OUTPUT_SLOT_1 = SpriteTexture.of(SPRITE).setSprite(0, 18, 18, 18);
    public static final IGuiTexture OUTPUT_SLOT_2 = SpriteTexture.of(SPRITE).setSprite(18, 18, 18, 18);
    public static final IGuiTexture OUTPUT_SLOT_3 = SpriteTexture.of(SPRITE).setSprite(36, 18, 18, 18);

    public static final IGuiTexture SLOT_M1 = SpriteTexture.of(SPRITE).setSprite(72, 0, 18, 18);
    public static final IGuiTexture SLOT_M2 = SpriteTexture.of(SPRITE).setSprite(0, 36, 18, 18);
    public static final IGuiTexture SLOT_M3 = SpriteTexture.of(SPRITE).setSprite(18, 36, 18, 18);
    public static final IGuiTexture SLOT_M4 = SpriteTexture.of(SPRITE).setSprite(36, 36, 18, 18);
    public static final IGuiTexture SLOT_M5 = SpriteTexture.of(SPRITE).setSprite(54, 36, 18, 18);
    public static final IGuiTexture SLOT_M6 = SpriteTexture.of(SPRITE).setSprite(72, 36, 18, 18);

    public static final IGuiTexture SLOT_PHANTOM_M1 = SpriteTexture.of(SPRITE).setSprite(72, 18, 18, 18);
    public static final IGuiTexture SLOT_PHANTOM_M2 = SpriteTexture.of(SPRITE).setSprite(0, 54, 18, 18);
    public static final IGuiTexture SLOT_PHANTOM_M3 = SpriteTexture.of(SPRITE).setSprite(18, 54, 18, 18);
    public static final IGuiTexture SLOT_PHANTOM_M4 = SpriteTexture.of(SPRITE).setSprite(36, 54, 18, 18);
    public static final IGuiTexture SLOT_PHANTOM_M5 = SpriteTexture.of(SPRITE).setSprite(54, 54, 18, 18);
    public static final IGuiTexture SLOT_PHANTOM_M6 = SpriteTexture.of(SPRITE).setSprite(72, 54, 18, 18);

    public static final IGuiTexture PHANTOM_ITEM_SLOT_OVERLAY = SpriteTexture.of(SPRITE).setSprite(54 + 1, 1, 16, 16);

    /**
     * Multitrack buffer item slot background for the given track row ({@code 0} = first track → {@link #SLOT_M1}).
     */
    @NotNull
    public static IGuiTexture multitrackItemSlotForTrack(int trackIndex) {
        return switch (trackIndex) {
            case 0 -> SLOT_M1;
            case 1 -> SLOT_M2;
            case 2 -> SLOT_M3;
            case 3 -> SLOT_M4;
            case 4 -> SLOT_M5;
            case 5 -> SLOT_M6;
            default -> INPUT_SLOT_1;
        };
    }

    /**
     * Multitrack buffer per-track filter (phantom) slot background.
     */
    @NotNull
    public static IGuiTexture multitrackPhantomFilterSlotForTrack(int trackIndex) {
        return switch (trackIndex) {
            case 0 -> SLOT_PHANTOM_M1;
            case 1 -> SLOT_PHANTOM_M2;
            case 2 -> SLOT_PHANTOM_M3;
            case 3 -> SLOT_PHANTOM_M4;
            case 4 -> SLOT_PHANTOM_M5;
            case 5 -> SLOT_PHANTOM_M6;
            default -> SLOT_PHANTOM_M1;
        };
    }
}
