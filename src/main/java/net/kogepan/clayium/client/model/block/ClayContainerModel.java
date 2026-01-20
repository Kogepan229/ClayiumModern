package net.kogepan.clayium.client.model.block;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blocks.TestClayContainerBlock;
import net.kogepan.clayium.client.utils.StaticFaceBakery;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    public static final ResourceLocation OUTPUT_1_OVERLAY = Clayium.id("block/overlay/output_1");
    public static final ResourceLocation INPUT_1_OVERLAY = Clayium.id("block/overlay/input_1");
    public static final ResourceLocation INPUT_CE_OVERLAY = Clayium.id("block/overlay/input_ce");

    private static @Nullable TextureAtlasSprite overlaySpriteOutput1;
    private static @Nullable TextureAtlasSprite overlaySpriteInput1;
    private static @Nullable TextureAtlasSprite overlaySpriteInputCE;

    public static void initSprites(TextureAtlas atlas) {
        overlaySpriteOutput1 = atlas.getSprite(OUTPUT_1_OVERLAY);
        overlaySpriteInput1 = atlas.getSprite(INPUT_1_OVERLAY);
        overlaySpriteInputCE = atlas.getSprite(INPUT_CE_OVERLAY);
    }

    private static final AABB OUTPUT_OVERLAY_AABB = new AABB(-0.001f, -0.001f, -0.001f,
            1.001f, 1.001f, 1.001f);
    private static final AABB INPUT_OVERLAY_AABB = new AABB(-0.002f, -0.002f, -0.002f,
            1.002f, 1.002f, 1.002f);

    private static final ChunkRenderTypeSet RENDER_TYPES = ChunkRenderTypeSet.of(RenderType.CUTOUT);

    static final ModelProperty<BlockAndTintGetter> MODEL_DATA_LEVEL = new ModelProperty<>();
    static final ModelProperty<BlockPos> MODEL_DATA_POS = new ModelProperty<>();

    private final BakedModel baseModel;
    @Nullable
    private final Map<Direction, BakedModel> bakedOverlayModels;

    private final BakedModel pipeCoreModel;
    private final Map<Direction, BakedModel> pipeArmModels;

    public ClayContainerModel(BakedModel base, @Nullable Map<Direction, BakedModel> overlays, BakedModel pipeCore,
                              Map<Direction, BakedModel> pipeArms) {
        this.baseModel = base;
        this.bakedOverlayModels = overlays;
        this.pipeCoreModel = pipeCore;
        this.pipeArmModels = pipeArms;
    }

    @Override
    @NotNull
    public ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state,
                                  @NotNull ModelData modelData) {
        ModelData.Builder builder = modelData.derive()
                .with(MODEL_DATA_LEVEL, level)
                .with(MODEL_DATA_POS, pos);

        return builder.build();
    }

    @Override
    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction,
                                    @NotNull RandomSource randomSource, @NotNull ModelData modelData,
                                    @Nullable RenderType renderType) {
        List<BakedQuad> quads;

        if (blockState == null || !blockState.getValue(TestClayContainerBlock.PIPE)) {
            quads = new ArrayList<>(
                    this.baseModel.getQuads(blockState, direction, randomSource, modelData, renderType));

            if (bakedOverlayModels != null) {
                Direction facing = blockState != null ? blockState.getValue(TestClayContainerBlock.FACING) :
                        Direction.NORTH;
                quads.addAll(bakedOverlayModels.get(facing).getQuads(blockState, direction, randomSource, modelData,
                        renderType));
            }

            if (renderType == RenderType.CUTOUT) {
                renderOverlays(quads, direction, modelData);
            }
        } else {
            quads = new ArrayList<>(
                    this.pipeCoreModel.getQuads(blockState, direction, randomSource, modelData, renderType));

            // 接続されている方向のアームを追加
            for (Direction armDirection : Direction.values()) {
                if (blockState.getValue(TestClayContainerBlock.getProperty(armDirection))) {
                    quads.addAll(this.pipeArmModels.get(armDirection).getQuads(blockState, direction, randomSource,
                            modelData, renderType));
                }
            }
        }

        return quads;
    }

    private void renderOverlays(@NotNull List<BakedQuad> quads, @Nullable Direction direction,
                                @NotNull ModelData modelData) {
        BlockAndTintGetter level = modelData.get(MODEL_DATA_LEVEL);
        BlockPos pos = modelData.get(MODEL_DATA_POS);
        if (level == null || pos == null) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ClayContainerBlockEntity container) {
            if (direction == null) {
                // for (Direction d : Direction.values()) {
                // renderOutputOverlay(quads, container, d);
                // renderInputOverlay(quads, container, d);
                // }
            } else {
                renderOutputOverlay(quads, container, direction);
                renderInputOverlay(quads, container, direction);
            }
        }
    }

    private static void renderOutputOverlay(@NotNull List<BakedQuad> quads, @NotNull ClayContainerBlockEntity container,
                                            @NotNull Direction direction) {
        MachineIOMode outputMode = container.getOutputMode(direction);
        var overlay = switch (outputMode) {
            case FIRST -> overlaySpriteOutput1;
            default -> null;
        };
        if (overlay != null) {
            quads.add(StaticFaceBakery.bakeFace(OUTPUT_OVERLAY_AABB, direction, overlay));
        }
    }

    private static void renderInputOverlay(@NotNull List<BakedQuad> quads, @NotNull ClayContainerBlockEntity container,
                                           @NotNull Direction direction) {
        MachineIOMode inputMode = container.getInputMode(direction);
        var overlay = switch (inputMode) {
            case FIRST -> overlaySpriteInput1;
            case CE -> overlaySpriteInputCE;
            default -> null;
        };
        if (overlay != null) {
            quads.add(StaticFaceBakery.bakeFace(INPUT_OVERLAY_AABB, direction, overlay));
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
