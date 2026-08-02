package net.kogepan.clayium.client.model.block;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.ClayContainerModelData;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

import com.mojang.math.Quadrant;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ClayContainerBlockStateModel implements DynamicBlockStateModel {

    private static final Direction[] DIRECTIONS = Direction.values();

    private final BlockStateModelPart baseModel;
    private final Map<Direction, BlockStateModelPart> importOverlays;
    private final Map<Direction, BlockStateModelPart> exportOverlays;
    private final Map<Direction, BlockStateModelPart> filterOverlays;
    private final int materialFlags;

    private ClayContainerBlockStateModel(
                                         BlockStateModelPart baseModel,
                                         Map<Direction, BlockStateModelPart> importOverlays,
                                         Map<Direction, BlockStateModelPart> exportOverlays,
                                         Map<Direction, BlockStateModelPart> filterOverlays) {
        this.baseModel = baseModel;
        this.importOverlays = importOverlays;
        this.exportOverlays = exportOverlays;
        this.filterOverlays = filterOverlays;
        int flags = baseModel.materialFlags();
        for (Direction side : DIRECTIONS) {
            flags |= importOverlays.get(side).materialFlags();
            flags |= exportOverlays.get(side).materialFlags();
            flags |= filterOverlays.get(side).materialFlags();
        }
        this.materialFlags = flags;
    }

    @Override
    public Object createGeometryKey(
                                    BlockAndTintGetter level,
                                    BlockPos pos,
                                    BlockState state,
                                    RandomSource random) {
        ClayContainerModelData data = level.getModelData(pos).get(ClayContainerModelData.PROPERTY);
        return new GeometryKey(this, data != null ? data : ClayContainerModelData.EMPTY);
    }

    @Override
    public void collectParts(
                             BlockAndTintGetter level,
                             BlockPos pos,
                             BlockState state,
                             RandomSource random,
                             List<BlockStateModelPart> parts) {
        parts.add(this.baseModel);
        ClayContainerModelData data = level.getModelData(pos).get(ClayContainerModelData.PROPERTY);
        if (data == null) {
            return;
        }
        for (Direction side : DIRECTIONS) {
            if (data.outputMode(side) != MachineIOMode.NONE) {
                parts.add(this.exportOverlays.get(side));
            }
            if (data.inputMode(side) != MachineIOMode.NONE) {
                parts.add(this.importOverlays.get(side));
            }
            if (data.hasFilter(side)) {
                parts.add(this.filterOverlays.get(side));
            }
        }
    }

    @Override
    public Material.Baked particleMaterial() {
        return this.baseModel.particleMaterial();
    }

    @Override
    @BakedQuad.MaterialFlags
    public int materialFlags() {
        return this.materialFlags;
    }

    private record GeometryKey(ClayContainerBlockStateModel model, ClayContainerModelData data) {}

    public record Unbaked(
                          Identifier baseModel,
                          Identifier importOverlay,
                          Identifier exportOverlay,
                          Identifier filterOverlay)
            implements CustomUnbakedBlockStateModel {

        public static final Identifier ID = Clayium.id("clay_container");
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("base_model").forGetter(Unbaked::baseModel),
                Identifier.CODEC.fieldOf("import_overlay").forGetter(Unbaked::importOverlay),
                Identifier.CODEC.fieldOf("export_overlay").forGetter(Unbaked::exportOverlay),
                Identifier.CODEC.fieldOf("filter_overlay").forGetter(Unbaked::filterOverlay))
                .apply(instance, Unbaked::new));

        @Override
        public BlockStateModel.UnbakedRoot asRoot() {
            // Shared part computations must not be nested inside the default root's ModelBaker.compute operation.
            return new Root(this);
        }

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new ClayContainerBlockStateModel(
                    bake(new Variant(this.baseModel), baker),
                    bakeForAllSides(this.importOverlay, baker),
                    bakeForAllSides(this.exportOverlay, baker),
                    bakeForAllSides(this.filterOverlay, baker));
        }

        private static Map<Direction, BlockStateModelPart> bakeForAllSides(
                                                                           Identifier model,
                                                                           ModelBaker baker) {
            Map<Direction, BlockStateModelPart> parts = new EnumMap<>(Direction.class);
            parts.put(Direction.NORTH, bake(new Variant(model), baker));
            parts.put(Direction.EAST, bake(new Variant(model).withYRot(Quadrant.R90), baker));
            parts.put(Direction.SOUTH, bake(new Variant(model).withYRot(Quadrant.R180), baker));
            parts.put(Direction.WEST, bake(new Variant(model).withYRot(Quadrant.R270), baker));
            parts.put(Direction.DOWN, bake(new Variant(model).withXRot(Quadrant.R90), baker));
            parts.put(Direction.UP, bake(new Variant(model).withXRot(Quadrant.R270), baker));
            return Map.copyOf(parts);
        }

        private static BlockStateModelPart bake(Variant variant, ModelBaker baker) {
            return baker.compute(new BakedVariantKey(variant));
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(this.baseModel);
            resolver.markDependency(this.importOverlay);
            resolver.markDependency(this.exportOverlay);
            resolver.markDependency(this.filterOverlay);
        }

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return MAP_CODEC;
        }

        private record BakedVariantKey(Variant variant) implements ModelBaker.SharedOperationKey<BlockStateModelPart> {

            @Override
            public BlockStateModelPart compute(ModelBaker baker) {
                return variant.bake(baker);
            }
        }

        private static final class Root implements BlockStateModel.UnbakedRoot {

            private final Unbaked model;
            private @Nullable BlockStateModel bakedModel;

            private Root(Unbaked model) {
                this.model = model;
            }

            @Override
            public synchronized BlockStateModel bake(BlockState state, ModelBaker baker) {
                if (this.bakedModel == null) {
                    this.bakedModel = this.model.bake(baker);
                }
                return this.bakedModel;
            }

            @Override
            public Object visualEqualityGroup(BlockState state) {
                return this;
            }

            @Override
            public void resolveDependencies(Resolver resolver) {
                this.model.resolveDependencies(resolver);
            }
        }
    }
}
