package net.kogepan.clayium.client.model.block;

import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.client.model.ModelTextures;
import net.kogepan.clayium.client.utils.StaticFaceBakery;
import net.kogepan.clayium.utils.MachineIOModes;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClayContainerModel implements IDynamicBakedModel {

    private static final AABB EXPORT_OVERLAY_AABB = new AABB(-0.001f, -0.001f, -0.001f,
            1.001f, 1.001f, 1.001f);
    private static final AABB IMPORT_OVERLAY_AABB = new AABB(-0.002f, -0.002f, -0.002f,
            1.002f, 1.002f, 1.002f);

    private static final ChunkRenderTypeSet RENDER_TYPES = ChunkRenderTypeSet.of(RenderType.CUTOUT);

    public static final ModelProperty<MachineIOModes> MODEL_DATA_IMPORT = new ModelProperty<>();
    public static final ModelProperty<MachineIOModes> MODEL_DATA_EXPORT = new ModelProperty<>();

    private final BakedModel baseModel;
    @Nullable
    private final Map<Direction, BakedModel> bakedOverlayModels;

    private final BakedModel pipeCoreModel;
    private final Map<Direction, BakedModel> pipeArmModels;

    private final boolean overlayItemOnly;

    public ClayContainerModel(BakedModel base, @Nullable Map<Direction, BakedModel> overlays, BakedModel pipeCore,
                              Map<Direction, BakedModel> pipeArms, boolean overlayItemOnly) {
        this.baseModel = base;
        this.bakedOverlayModels = overlays;
        this.pipeCoreModel = pipeCore;
        this.pipeArmModels = pipeArms;
        this.overlayItemOnly = overlayItemOnly;
    }

    @Override
    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction,
                                    @NotNull RandomSource randomSource, @NotNull ModelData modelData,
                                    @Nullable RenderType renderType) {
        List<BakedQuad> quads;

        if (blockState == null || !blockState.getValue(ClayContainerBlock.PIPE)) {
            quads = new ArrayList<>(
                    this.baseModel.getQuads(blockState, direction, randomSource, modelData, renderType));

            if (bakedOverlayModels != null) {
                boolean shouldRenderOverlayModel = !overlayItemOnly || blockState == null;
                if (shouldRenderOverlayModel) {
                    Direction facing = Direction.NORTH;
                    if (blockState != null && blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
                        facing = blockState.getValue(containerBlock.getFacingProperty());
                    }
                    quads.addAll(bakedOverlayModels.get(facing).getQuads(blockState, direction, randomSource,
                            modelData, renderType));
                }
            }

            if (renderType == RenderType.CUTOUT) {
                renderOverlays(quads, direction, modelData);
            }
        } else {
            quads = new ArrayList<>();

            // Core
            for (var quad : this.pipeCoreModel.getQuads(blockState, direction, randomSource, modelData, renderType)) {
                if (!blockState.getValue(ClayContainerBlock.getProperty(quad.getDirection()))) {
                    quads.add(quad);
                }
            }

            // Side cubes
            for (Direction armDirection : Direction.values()) {
                if (blockState.getValue(ClayContainerBlock.getProperty(armDirection))) {
                    quads.addAll(this.pipeArmModels.get(armDirection).getQuads(blockState, direction, randomSource,
                            modelData, renderType));
                }
            }
        }

        return quads;
    }

    private void renderOverlays(@NotNull List<BakedQuad> quads, @Nullable Direction direction,
                                @NotNull ModelData modelData) {
        if (direction == null) return;

        MachineIOModes importModes = modelData.get(MODEL_DATA_IMPORT);
        MachineIOModes exportModes = modelData.get(MODEL_DATA_EXPORT);
        if (importModes == null || exportModes == null) return;

        renderExportOverlay(quads, exportModes, direction);
        renderImportOverlay(quads, importModes, direction);
    }

    private static void renderExportOverlay(@NotNull List<BakedQuad> quads, @NotNull MachineIOModes exportModes,
                                            @NotNull Direction direction) {
        TextureAtlasSprite overlay = ModelTextures.getOverlayExportSprite(exportModes.getMode(direction));
        if (overlay != null) {
            quads.add(StaticFaceBakery.bakeFace(EXPORT_OVERLAY_AABB, direction, overlay));
        }
    }

    private static void renderImportOverlay(@NotNull List<BakedQuad> quads, @NotNull MachineIOModes importModes,
                                            @NotNull Direction direction) {
        TextureAtlasSprite overlay = ModelTextures.getOverlayImportSprite(importModes.getMode(direction));
        if (overlay != null) {
            quads.add(StaticFaceBakery.bakeFace(IMPORT_OVERLAY_AABB, direction, overlay));
        }
    }

    @Override
    @NotNull
    public BakedModel applyTransform(@NotNull ItemDisplayContext transformType, @NotNull PoseStack poseStack,
                                     boolean applyLeftHandTransform) {
        this.baseModel.applyTransform(transformType, poseStack, applyLeftHandTransform);
        return this;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    @NotNull
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleIcon() {
        return this.baseModel.getParticleIcon();
    }

    @Override
    @NotNull
    @OnlyIn(Dist.CLIENT)
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    @NotNull
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand,
                                             @NotNull ModelData data) {
        return RENDER_TYPES;
    }
}
