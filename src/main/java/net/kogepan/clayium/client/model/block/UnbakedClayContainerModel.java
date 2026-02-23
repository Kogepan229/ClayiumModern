package net.kogepan.clayium.client.model.block;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.client.utils.ModelUtils;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class UnbakedClayContainerModel implements IUnbakedGeometry<UnbakedClayContainerModel> {

    ResourceLocation PIPE_CORE_MODEL = Clayium.id("block/pipe/core");
    ResourceLocation PIPE_NORTH_MODEL = Clayium.id("block/pipe/north");
    ResourceLocation PIPE_WEST_MODEL = Clayium.id("block/pipe/west");
    ResourceLocation PIPE_SOUTH_MODEL = Clayium.id("block/pipe/south");
    ResourceLocation PIPE_EAST_MODEL = Clayium.id("block/pipe/east");
    ResourceLocation PIPE_UP_MODEL = Clayium.id("block/pipe/up");
    ResourceLocation PIPE_DOWN_MODEL = Clayium.id("block/pipe/down");

    private final BlockModel base;
    @Nullable
    private final BlockModel overlay;
    @NotNull
    private final Map<String, BlockModel> overlays;
    private final boolean rotateVertical;
    private final boolean overlayItemOnly;

    public UnbakedClayContainerModel(BlockModel base, @Nullable BlockModel overlay) {
        this(base, overlay, Map.of(), false, false);
    }

    public UnbakedClayContainerModel(BlockModel base, @Nullable BlockModel overlay, boolean rotateVertical) {
        this(base, overlay, Map.of(), rotateVertical, false);
    }

    public UnbakedClayContainerModel(BlockModel base, @Nullable BlockModel overlay, boolean rotateVertical,
                                     boolean overlayItemOnly) {
        this(base, overlay, Map.of(), rotateVertical, overlayItemOnly);
    }

    public UnbakedClayContainerModel(BlockModel base, @Nullable BlockModel overlay,
                                     @NotNull Map<String, BlockModel> overlays,
                                     boolean rotateVertical, boolean overlayItemOnly) {
        this.base = base;
        this.overlay = overlay;
        this.overlays = overlays;
        this.rotateVertical = rotateVertical;
        this.overlayItemOnly = overlayItemOnly;
    }

    @Override
    @NotNull
    public BakedModel bake(@NotNull IGeometryBakingContext context, @NotNull ModelBaker baker,
                           @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelState,
                           @NotNull ItemOverrides overrides) {
        BakedModel bakedBase = this.base.bake(baker, spriteGetter, modelState);

        Map<String, Map<Direction, BakedModel>> bakedOverlays = new java.util.LinkedHashMap<>();
        if (this.overlay != null) {
            bakedOverlays.put(ClayContainerModel.DEFAULT_FRONT_OVERLAY_VARIANT,
                    bakeOverlay(this.overlay, baker, spriteGetter, context));
        }
        for (var entry : this.overlays.entrySet()) {
            bakedOverlays.put(entry.getKey(), bakeOverlay(entry.getValue(), baker, spriteGetter, context));
        }
        if (bakedOverlays.isEmpty()) {
            bakedOverlays = null;
        }

        BakedModel pipeCore = new BakedPipeModel(bakedBase, baker.bake(PIPE_CORE_MODEL, modelState, spriteGetter));
        Map<Direction, BakedModel> pipeArms = new EnumMap<>(Direction.class);
        pipeArms.put(Direction.NORTH,
                new BakedPipeModel(bakedBase, baker.bake(PIPE_NORTH_MODEL, modelState, spriteGetter)));
        pipeArms.put(Direction.WEST,
                new BakedPipeModel(bakedBase, baker.bake(PIPE_WEST_MODEL, modelState, spriteGetter)));
        pipeArms.put(Direction.SOUTH,
                new BakedPipeModel(bakedBase, baker.bake(PIPE_SOUTH_MODEL, modelState, spriteGetter)));
        pipeArms.put(Direction.EAST,
                new BakedPipeModel(bakedBase, baker.bake(PIPE_EAST_MODEL, modelState, spriteGetter)));
        pipeArms.put(Direction.UP, new BakedPipeModel(bakedBase, baker.bake(PIPE_UP_MODEL, modelState, spriteGetter)));
        pipeArms.put(Direction.DOWN,
                new BakedPipeModel(bakedBase, baker.bake(PIPE_DOWN_MODEL, modelState, spriteGetter)));

        return new ClayContainerModel(bakedBase, bakedOverlays, pipeCore, pipeArms, overlayItemOnly);
    }

    @Override
    public void resolveParents(@NotNull Function<ResourceLocation, UnbakedModel> modelGetter,
                               @NotNull IGeometryBakingContext context) {
        this.base.resolveParents(modelGetter);
        if (this.overlay != null) {
            this.overlay.resolveParents(modelGetter);
        }
        for (var overlayModel : this.overlays.values()) {
            overlayModel.resolveParents(modelGetter);
        }
    }

    @NotNull
    private Map<Direction, BakedModel> bakeOverlay(@NotNull BlockModel overlay, @NotNull ModelBaker baker,
                                                   @NotNull Function<Material, TextureAtlasSprite> spriteGetter,
                                                   @NotNull IGeometryBakingContext context) {
        Map<Direction, BakedModel> bakedOverlay = new EnumMap<>(Direction.class);
        bakedOverlay.put(Direction.NORTH,
                ModelUtils.rotateModel(overlay, baker, spriteGetter, context.getRootTransform(), 0));
        bakedOverlay.put(Direction.WEST,
                ModelUtils.rotateModel(overlay, baker, spriteGetter, context.getRootTransform(), 90));
        bakedOverlay.put(Direction.SOUTH,
                ModelUtils.rotateModel(overlay, baker, spriteGetter, context.getRootTransform(), 180));
        bakedOverlay.put(Direction.EAST,
                ModelUtils.rotateModel(overlay, baker, spriteGetter, context.getRootTransform(), 270));

        if (this.rotateVertical) {
            bakedOverlay.put(Direction.UP,
                    ModelUtils.rotateModelVertical(overlay, baker, spriteGetter, context.getRootTransform(), 90));
            bakedOverlay.put(Direction.DOWN,
                    ModelUtils.rotateModelVertical(overlay, baker, spriteGetter, context.getRootTransform(), -90));
        }
        return bakedOverlay;
    }
}
