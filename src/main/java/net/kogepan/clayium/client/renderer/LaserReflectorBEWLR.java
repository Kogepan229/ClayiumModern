package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;

/**
 * BlockEntityWithoutLevelRenderer for Laser Reflector item.
 * Renders the same pyramid shape as the block.
 */
@OnlyIn(Dist.CLIENT)
public class LaserReflectorBEWLR extends BlockEntityWithoutLevelRenderer {

    public LaserReflectorBEWLR(BlockEntityRenderDispatcher blockEntityRenderDispatcher,
                               net.minecraft.client.model.geom.EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem &&
                blockItem.getBlock() == ClayiumBlocks.LASER_REFLECTOR.get()) {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(LaserReflectorRenderer.TEXTURE);

            poseStack.pushPose();
            if (displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.FIXED) {
                // Tilted view for inventory/display
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(115.0f));
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(53.0f));
                poseStack.translate(-0.5, -0.5, -0.5);
                poseStack.translate(0.0, 0.1, 0.0);
            } else if (displayContext == ItemDisplayContext.GROUND) {
                // Horizontal on ground (apex pointing sideways) when dropped
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.translate(-0.5, -0.5, -0.5);
            } else if (displayContext.firstPerson() || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
                    displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                        // Horizontal when held in hand (apex pointing right), base tilted slightly back
                        poseStack.translate(0.5, 0.5, 0.5);
                        poseStack.scale(0.5f, 0.5f, 0.5f);
                        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-15.0f)); // Base toward back
                        poseStack.translate(-0.5, -0.5, -0.5);
                    }

            Direction pyramidFacing;
            if (displayContext == ItemDisplayContext.GROUND) {
                pyramidFacing = Direction.NORTH;
            } else if (displayContext.firstPerson() || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
                    displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                        pyramidFacing = Direction.EAST; // Right when facing north
                    } else {
                        pyramidFacing = Direction.UP;
                    }
            int light = (displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.FIXED) ?
                    LightTexture.FULL_BRIGHT : packedLight;
            Integer dimmedFace = (displayContext == ItemDisplayContext.GUI ||
                    displayContext == ItemDisplayContext.FIXED) ? LaserReflectorRenderer.GUI_FRONT_FACE_INDEX : null;
            int dimmedColor = 200; // Slightly darker for front face in GUI
            LaserReflectorRenderer.renderPyramid(poseStack, buffer, light, packedOverlay,
                    pyramidFacing, sprite, dimmedFace, dimmedColor);
            poseStack.popPose();
        } else {
            super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
        }
    }
}
