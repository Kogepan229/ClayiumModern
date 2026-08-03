package net.kogepan.clayium.client.model.block;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.ClayContainerModelData;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

import com.mojang.math.Quadrant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ClayContainerBlockStateModel implements DynamicBlockStateModel {

    private static final Direction[] DIRECTIONS = Direction.values();

    private final Map<Direction, BlockStateModelPart> baseModels;
    private final Map<MachineIOMode, Map<Direction, BlockStateModelPart>> importOverlays;
    private final Map<MachineIOMode, Map<Direction, BlockStateModelPart>> exportOverlays;
    private final Map<Direction, BlockStateModelPart> filterOverlays;
    private final Map<Direction, PipeParts> pipeParts;
    private final int materialFlags;

    private ClayContainerBlockStateModel(
                                         Map<Direction, BlockStateModelPart> baseModels,
                                         Map<MachineIOMode, Map<Direction, BlockStateModelPart>> importOverlays,
                                         Map<MachineIOMode, Map<Direction, BlockStateModelPart>> exportOverlays,
                                         Map<Direction, BlockStateModelPart> filterOverlays,
                                         Map<Direction, PipeParts> pipeParts) {
        this.baseModels = baseModels;
        this.importOverlays = importOverlays;
        this.exportOverlays = exportOverlays;
        this.filterOverlays = filterOverlays;
        this.pipeParts = pipeParts;
        int flags = 0;
        for (BlockStateModelPart part : baseModels.values()) {
            flags |= part.materialFlags();
        }
        for (Map<Direction, BlockStateModelPart> overlays : importOverlays.values()) {
            for (BlockStateModelPart part : overlays.values()) {
                flags |= part.materialFlags();
            }
        }
        for (Map<Direction, BlockStateModelPart> overlays : exportOverlays.values()) {
            for (BlockStateModelPart part : overlays.values()) {
                flags |= part.materialFlags();
            }
        }
        for (BlockStateModelPart part : filterOverlays.values()) {
            flags |= part.materialFlags();
        }
        for (PipeParts parts : pipeParts.values()) {
            for (BlockStateModelPart part : parts.coreFaces.values()) {
                flags |= part.materialFlags();
            }
            for (BlockStateModelPart part : parts.arms.values()) {
                flags |= part.materialFlags();
            }
        }
        this.materialFlags = flags;
    }

    @Override
    public Object createGeometryKey(
                                    BlockAndTintGetter level,
                                    BlockPos pos,
                                    BlockState state,
                                    RandomSource random) {
        boolean pipe = state.getValue(ClayContainerBlock.PIPE);
        ClayContainerModelData data = pipe ? ClayContainerModelData.EMPTY :
                level.getModelData(pos).get(ClayContainerModelData.PROPERTY);
        return new GeometryKey(
                this,
                data != null ? data : ClayContainerModelData.EMPTY,
                state.getValue(ClayContainerBlock.FACING),
                pipeConnectionMask(state));
    }

    @Override
    public void collectParts(
                             BlockAndTintGetter level,
                             BlockPos pos,
                             BlockState state,
                             RandomSource random,
                             List<BlockStateModelPart> parts) {
        if (state.getValue(ClayContainerBlock.PIPE)) {
            PipeParts selectedPipeParts = this.pipeParts.get(state.getValue(ClayContainerBlock.FACING));
            for (Direction side : DIRECTIONS) {
                if (state.getValue(ClayContainerBlock.getConnectionProperty(side))) {
                    parts.add(selectedPipeParts.arms.get(side));
                } else {
                    parts.add(selectedPipeParts.coreFaces.get(side));
                }
            }
            return;
        }

        parts.add(this.baseModels.get(state.getValue(ClayContainerBlock.FACING)));
        ClayContainerModelData data = level.getModelData(pos).get(ClayContainerModelData.PROPERTY);
        if (data == null) {
            return;
        }
        for (Direction side : DIRECTIONS) {
            Map<Direction, BlockStateModelPart> outputParts = this.exportOverlays.get(data.outputMode(side));
            if (outputParts != null) {
                parts.add(outputParts.get(side));
            }
            Map<Direction, BlockStateModelPart> inputParts = this.importOverlays.get(data.inputMode(side));
            if (inputParts != null) {
                parts.add(inputParts.get(side));
            }
            if (data.hasFilter(side)) {
                parts.add(this.filterOverlays.get(side));
            }
        }
    }

    @Override
    public Material.Baked particleMaterial() {
        return this.baseModels.get(Direction.NORTH).particleMaterial();
    }

    @Override
    @BakedQuad.MaterialFlags
    public int materialFlags() {
        return this.materialFlags;
    }

    private record GeometryKey(
                               ClayContainerBlockStateModel model,
                               ClayContainerModelData data,
                               Direction facing,
                               int pipeConnectionMask) {}

    private record PipeParts(
                             Map<Direction, BlockStateModelPart> coreFaces,
                             Map<Direction, BlockStateModelPart> arms) {}

    private record PipePart(
                            List<BakedQuad> quads,
                            Material.Baked particleMaterial,
                            TriState ambientOcclusion,
                            int materialFlags)
            implements BlockStateModelPart {

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction direction) {
            return direction == null ? this.quads : List.of();
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean useAmbientOcclusion() {
            return this.ambientOcclusion != TriState.FALSE;
        }
    }

    public record Unbaked(
                          Identifier baseModel,
                          Map<MachineIOMode, Identifier> importOverlays,
                          Map<MachineIOMode, Identifier> exportOverlays,
                          Identifier filterOverlay)
            implements CustomUnbakedBlockStateModel {

        public static final Identifier ID = Clayium.id("clay_container");
        private static final Codec<MachineIOMode> MODE_NAME_CODEC = Codec.STRING.comapFlatMap(name -> {
            try {
                return DataResult.success(MachineIOMode.valueOf(name.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException exception) {
                return DataResult.error(() -> "Unknown machine I/O mode: " + name);
            }
        }, mode -> mode.name().toLowerCase(Locale.ROOT));
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("base_model").forGetter(Unbaked::baseModel),
                Codec.unboundedMap(MODE_NAME_CODEC, Identifier.CODEC).fieldOf("import_overlays")
                        .forGetter(Unbaked::importOverlays),
                Codec.unboundedMap(MODE_NAME_CODEC, Identifier.CODEC).fieldOf("export_overlays")
                        .forGetter(Unbaked::exportOverlays),
                Identifier.CODEC.fieldOf("filter_overlay").forGetter(Unbaked::filterOverlay))
                .apply(instance, Unbaked::new));

        public Unbaked {
            importOverlays = Map.copyOf(importOverlays);
            exportOverlays = Map.copyOf(exportOverlays);
        }

        @Override
        public BlockStateModel.UnbakedRoot asRoot() {
            // Shared part computations must not be nested inside the default root's ModelBaker.compute operation.
            return new Root(this);
        }

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            Map<Direction, BlockStateModelPart> baseModels = bakeForHorizontalSides(this.baseModel, baker);
            return new ClayContainerBlockStateModel(
                    baseModels,
                    bakeOverlays(this.importOverlays, baker),
                    bakeOverlays(this.exportOverlays, baker),
                    bakeForAllSides(this.filterOverlay, baker),
                    bakePipeParts(baseModels, baker));
        }

        private static Map<Direction, BlockStateModelPart> bakeForHorizontalSides(
                                                                                  Identifier model,
                                                                                  ModelBaker baker) {
            Map<Direction, BlockStateModelPart> parts = new EnumMap<>(Direction.class);
            parts.put(Direction.NORTH, bake(new Variant(model), baker));
            parts.put(Direction.EAST, bake(new Variant(model).withYRot(Quadrant.R90), baker));
            parts.put(Direction.SOUTH, bake(new Variant(model).withYRot(Quadrant.R180), baker));
            parts.put(Direction.WEST, bake(new Variant(model).withYRot(Quadrant.R270), baker));
            return Map.copyOf(parts);
        }

        private static Map<MachineIOMode, Map<Direction, BlockStateModelPart>> bakeOverlays(
                                                                                            Map<MachineIOMode, Identifier> models,
                                                                                            ModelBaker baker) {
            Map<MachineIOMode, Map<Direction, BlockStateModelPart>> overlays = new EnumMap<>(MachineIOMode.class);
            models.forEach((mode, model) -> overlays.put(mode, bakeForAllSides(model, baker)));
            return Map.copyOf(overlays);
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

        private static Map<Direction, PipeParts> bakePipeParts(
                                                               Map<Direction, BlockStateModelPart> baseModels,
                                                               ModelBaker baker) {
            Map<Direction, PipeParts> byFacing = new EnumMap<>(Direction.class);
            baseModels.forEach((facing, base) -> {
                Map<Direction, BlockStateModelPart> coreFaces = new EnumMap<>(Direction.class);
                Map<Direction, BlockStateModelPart> arms = new EnumMap<>(Direction.class);
                for (Direction side : DIRECTIONS) {
                    Vector3f[] armBounds = armBounds(side);
                    coreFaces.put(
                            side,
                            bakeCuboidPart(
                                    new Vector3f(5, 5, 5),
                                    new Vector3f(11, 11, 11),
                                    List.of(side),
                                    base,
                                    baker));
                    arms.put(
                            side,
                            bakeCuboidPart(
                                    armBounds[0],
                                    armBounds[1],
                                    java.util.Arrays.stream(DIRECTIONS)
                                            .filter(face -> face != side.getOpposite())
                                            .toList(),
                                    base,
                                    baker));
                }
                byFacing.put(facing, new PipeParts(Map.copyOf(coreFaces), Map.copyOf(arms)));
            });
            return Map.copyOf(byFacing);
        }

        private static BlockStateModelPart bakeCuboidPart(
                                                          Vector3f from,
                                                          Vector3f to,
                                                          List<Direction> includedFaces,
                                                          BlockStateModelPart base,
                                                          ModelBaker baker) {
            List<BakedQuad> quads = new java.util.ArrayList<>();
            int flags = 0;
            for (Direction face : DIRECTIONS) {
                if (!includedFaces.contains(face)) {
                    continue;
                }
                BakedQuad baseQuad = findFaceQuad(base, face);
                if (baseQuad == null) {
                    continue;
                }
                BakedQuad quad = FaceBakery.bakeQuad(
                        baker.interner(),
                        from,
                        to,
                        FaceBakery.defaultFaceUV(from, to, face),
                        Quadrant.R0,
                        baseQuad.materialInfo(),
                        face,
                        BlockModelRotation.IDENTITY,
                        null,
                        net.neoforged.neoforge.client.model.ExtraFaceData.DEFAULT);
                quads.add(quad);
                flags |= quad.materialInfo().flags();
            }
            return new PipePart(List.copyOf(quads), base.particleMaterial(), base.ambientOcclusion(), flags);
        }

        private static @Nullable BakedQuad findFaceQuad(BlockStateModelPart part, Direction face) {
            List<BakedQuad> culledQuads = part.getQuads(face);
            if (!culledQuads.isEmpty()) {
                return culledQuads.getFirst();
            }
            for (BakedQuad quad : part.getQuads(null)) {
                if (quad.direction() == face) {
                    return quad;
                }
            }
            return null;
        }

        private static Vector3f[] armBounds(Direction side) {
            return switch (side) {
                case DOWN -> new Vector3f[] { new Vector3f(5, 0, 5), new Vector3f(11, 5, 11) };
                case UP -> new Vector3f[] { new Vector3f(5, 11, 5), new Vector3f(11, 16, 11) };
                case NORTH -> new Vector3f[] { new Vector3f(5, 5, 0), new Vector3f(11, 11, 5) };
                case SOUTH -> new Vector3f[] { new Vector3f(5, 5, 11), new Vector3f(11, 11, 16) };
                case WEST -> new Vector3f[] { new Vector3f(0, 5, 5), new Vector3f(5, 11, 11) };
                case EAST -> new Vector3f[] { new Vector3f(11, 5, 5), new Vector3f(16, 11, 11) };
            };
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(this.baseModel);
            this.importOverlays.values().forEach(resolver::markDependency);
            this.exportOverlays.values().forEach(resolver::markDependency);
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

    private static int pipeConnectionMask(BlockState state) {
        if (!state.getValue(ClayContainerBlock.PIPE)) {
            return -1;
        }
        int mask = 0;
        for (Direction side : DIRECTIONS) {
            if (state.getValue(ClayContainerBlock.getConnectionProperty(side))) {
                mask |= 1 << side.get3DDataValue();
            }
        }
        return mask;
    }
}
