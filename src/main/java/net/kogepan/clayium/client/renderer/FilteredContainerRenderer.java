package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.StorageContainerBlockEntity;
import net.kogepan.clayium.blockentities.VacuumContainerBlockEntity;
import net.kogepan.clayium.blocks.AbstractFilteredContainerBlock;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.items.blockitem.StorageContainerBlockItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.model.data.ModelData;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.jetbrains.annotations.NotNull;

import static net.kogepan.clayium.client.model.block.ClayContainerModel.MODEL_DATA_FRONT_OVERLAY_VARIANT;

/** Renders the displayed item and amount on the front of filtered containers. */
public final class FilteredContainerRenderer {

    // Includes the EntityItem bob and RenderItem.renderInFrame transforms used by Original.
    private static final float BLOCK_ITEM_Y = -0.03F + 0.1F + 1.25F * 0.05F;
    private static final float FLAT_ITEM_Y = -0.03F + 0.1F + 0.5128205F * (-0.05F + 0.25F);
    private static final float BLOCK_ITEM_SCALE = 0.625F;
    private static final float FLAT_ITEM_SCALE = 0.5128205F;
    private static final float FIXED_BLOCK_MODEL_SCALE = 0.5F;
    private static final float TEXT_SCALE = 0.016666668F * 1.6F;
    private static final String[] AMOUNT_SUFFIXES = { "", "k", "M", "G", "T", "P", "E", "Z", "Y" };

    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderer;
    private final Font font;

    public FilteredContainerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.font = context.getFont();
    }

    public void render(@NotNull ClayContainerBlockEntity blockEntity, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack displayedItem;
        int displayedAmount;
        if (blockEntity instanceof StorageContainerBlockEntity storage) {
            displayedItem = storage.getDisplayedItem();
            displayedAmount = storage.getStoredAmount();
        } else if (blockEntity instanceof VacuumContainerBlockEntity vacuum) {
            displayedItem = vacuum.getDisplayedItem();
            displayedAmount = 0;
        } else {
            return;
        }

        if (displayedItem.isEmpty()) {
            return;
        }

        if (!(blockEntity.getBlockState().getBlock() instanceof ClayContainerBlock containerBlock)) {
            return;
        }
        Direction facing = blockEntity.getBlockState().getValue(containerBlock.getFacingProperty());
        int displayLight = blockEntity.getLevel() == null ? packedLight : LevelRenderer.getLightColor(
                blockEntity.getLevel(),
                blockEntity.getBlockState(),
                blockEntity.getBlockPos().relative(facing));

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        this.renderItem(blockEntity, displayedItem, poseStack, buffer, displayLight, packedOverlay);
        this.renderText(
                blockEntity.getBlockPos(),
                displayedItem,
                displayedAmount,
                isPipingMode(),
                poseStack,
                buffer,
                LightTexture.FULL_BRIGHT);

        poseStack.popPose();
    }

    private void renderItem(@NotNull ClayContainerBlockEntity blockEntity, @NotNull ItemStack displayedItem,
                            @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                            int packedLight, int packedOverlay) {
        ItemStack renderedItem = displayedItem.copyWithCount(1);
        int seed = blockEntity.getBlockPos().hashCode();
        BakedModel model = this.itemRenderer.getModel(renderedItem, blockEntity.getLevel(), null, seed);
        boolean isBlockModel = renderedItem.getItem() instanceof BlockItem && model.isGui3d();
        float itemY = isBlockModel ? BLOCK_ITEM_Y : FLAT_ITEM_Y;
        float itemScale = isBlockModel ? BLOCK_ITEM_SCALE : FLAT_ITEM_SCALE;

        poseStack.pushPose();
        poseStack.translate(0.0F, itemY, 0.511F);
        poseStack.scale(itemScale, itemScale, itemScale);
        if (!this.renderFilteredContainerBlock(renderedItem, poseStack, buffer, packedLight, packedOverlay)) {
            this.itemRenderer.render(
                    renderedItem,
                    ItemDisplayContext.FIXED,
                    false,
                    poseStack,
                    buffer,
                    packedLight,
                    packedOverlay,
                    model);
        }
        poseStack.popPose();
    }

    private boolean renderFilteredContainerBlock(@NotNull ItemStack itemStack, @NotNull PoseStack poseStack,
                                                 @NotNull MultiBufferSource buffer, int packedLight,
                                                 int packedOverlay) {
        if (!(itemStack.getItem() instanceof BlockItem blockItem) ||
                !(blockItem.getBlock() instanceof AbstractFilteredContainerBlock containerBlock)) {
            return false;
        }

        BlockState blockState = containerBlock.defaultBlockState()
                .setValue(containerBlock.getFacingProperty(), Direction.SOUTH);
        ModelData modelData = ModelData.EMPTY;
        if (itemStack.getItem() instanceof StorageContainerBlockItem &&
                StorageContainerBlockItem.isUpgraded(itemStack)) {
            modelData = ModelData.builder()
                    .with(MODEL_DATA_FRONT_OVERLAY_VARIANT, "upgraded")
                    .build();
        }

        poseStack.pushPose();
        poseStack.scale(FIXED_BLOCK_MODEL_SCALE, FIXED_BLOCK_MODEL_SCALE, FIXED_BLOCK_MODEL_SCALE);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        this.blockRenderer.renderSingleBlock(
                blockState, poseStack, buffer, packedLight, packedOverlay, modelData, null);
        poseStack.popPose();
        return true;
    }

    private void renderText(@NotNull BlockPos blockPos, @NotNull ItemStack displayedItem, int displayedAmount,
                            boolean piping,
                            @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.translate(0.0F, -0.15F, -0.552F);
        poseStack.scale(-TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);

        String amountText = formatAmount(displayedAmount);
        this.font.drawInBatch(
                amountText,
                -this.font.width(amountText) / 2.0F,
                0.0F,
                0xDC000000,
                false,
                poseStack.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                packedLight);

        if (piping || isTargeted(blockPos)) {
            this.renderName(displayedItem.getHoverName(), poseStack, buffer, packedLight);
        }
        poseStack.popPose();
    }

    private void renderName(@NotNull Component name, @NotNull PoseStack poseStack,
                            @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.translate(0.0F, -20.0F, -5.0F);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        int width = this.font.width(name);
        if (width > 64) {
            poseStack.scale(64.0F / width, 1.0F, 1.0F);
        }
        this.font.drawInBatch(
                name,
                -width / 2.0F,
                0.0F,
                0xFFFFFFFF,
                false,
                poseStack.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0x80000000,
                packedLight);
    }

    private static boolean isTargeted(@NotNull BlockPos blockPos) {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        return hitResult != null && hitResult.getType() == HitResult.Type.BLOCK &&
                hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getBlockPos().equals(blockPos);
    }

    private static boolean isPipingMode() {
        var player = Minecraft.getInstance().player;
        return player != null && PipedMachineIoRenderer.isPipingTool(player.getMainHandItem());
    }

    @NotNull
    private static String formatAmount(int amount) {
        long value = Math.max(0L, amount);
        if (value == 0L) {
            return "0";
        }

        int exponent = (int) Math.floor(Math.log10(value));
        if (exponent < 5) {
            return Long.toString(value);
        }

        int suffix = Math.min(exponent / 3, AMOUNT_SUFFIXES.length - 1);
        int digits = (int) (value / Math.pow(10.0D, exponent - 2));
        return digits / 100 + (exponent % 3 == 0 ? "." : "") + digits / 10 % 10 + (exponent % 3 == 1 ? "." : "") +
                digits % 10 + AMOUNT_SUFFIXES[suffix];
    }
}
