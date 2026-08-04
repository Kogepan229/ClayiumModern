package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.api.block.ICAReactorCoil;
import net.kogepan.clayium.api.block.ICAReactorHull;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.CAReactorRecipeLogic;
import net.kogepan.clayium.capability.ISynchronizedInterface;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.network.payload.CAReactorWorkingPayload;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;

/** CA Reactor controller with arbitrary ring structure validation and rank-scaled processing. */
public class CAReactorBlockEntity extends AbstractMultiblockMachineBlockEntity {

    public static final int MAX_COILS = 128;
    public static final int REQUIRED_HULLS = 50;

    private static final int WORKING_STOP_DELAY_TICKS = 4;
    private static final double MAXIMUM_REACTOR_GLOW_INFLATE = 0.5D;
    private static final double EFFICIENCY = 0.2D;
    private static final double EFFICIENCY_BASE_PER_HULL = 1.02D;
    private static final double EFFICIENCY_BASE_PER_RANK = 7.5D;
    private static final double ENERGY_BASE = 1.01D;

    private static final String REACTOR_RANK_TAG = "reactorRank";
    private static final String REACTOR_HULL_COUNT_TAG = "reactorHullCount";
    private static final String REACTOR_EFFICIENCY_TAG = "reactorEfficiency";
    private static final String REACTOR_WORKING_TAG = "reactorWorking";
    private static final String REACTOR_GLOW_POSITIONS_TAG = "reactorGlowPositions";

    private static final List<BlockPos> COIL_NEIGHBOR_OFFSETS = createCoilNeighborOffsets();

    private double reactorRankIndex;
    private int reactorHullCount;
    private double reactorEfficiency;
    private double cePerTickMultiplier = 1.0D;
    @NotNull
    private List<BlockPos> reactorGlowPositions = List.of();
    @NotNull
    private AABB reactorRenderBoundingBox;
    @NotNull
    private Component structureError = Component.translatable("message.clayium.ca_reactor.no_near_coil");
    private boolean reactorWorking;
    private int workingStopDelay;
    private int reactorRenderRevision;
    private boolean structureSyncDirty = true;

    public CAReactorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CA_REACTOR_BLOCK_ENTITY.get(), pos, blockState,
                getValidInputModes(1),
                getValidOutputModes(1),
                1,
                1,
                be -> new CAReactorRecipeLogic(
                        (CAReactorBlockEntity) be,
                        ClayiumRecipeTypes.CA_REACTOR_RECIPE_TYPE.get(),
                        be.getEnergyHolder()),
                ClayiumRecipeTypes.CA_REACTOR_RECIPE_TYPE.get());
        this.reactorRenderBoundingBox = new AABB(pos).inflate(MAXIMUM_REACTOR_GLOW_INFLATE);
    }

    @Override
    protected void tick() {
        super.tick();
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        boolean working = ((CAReactorRecipeLogic) this.recipeLogic).workedThisTick();
        if (working) {
            this.workingStopDelay = WORKING_STOP_DELAY_TICKS;
            this.updateServerWorkingState(true);
        } else if (this.reactorWorking && this.workingStopDelay > 0) {
            this.workingStopDelay--;
            if (this.workingStopDelay == 0) {
                this.updateServerWorkingState(false);
            }
        } else {
            this.updateServerWorkingState(false);
        }
        if (this.structureSyncDirty) {
            this.structureSyncDirty = false;
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_CLIENTS);
        }
    }

    private void updateServerWorkingState(boolean working) {
        if (this.reactorWorking == working) {
            return;
        }
        this.reactorWorking = working;
        if (this.level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(this.worldPosition),
                    new CAReactorWorkingPayload(this.worldPosition, working));
        }
    }

    @Override
    public boolean canUseOverclockers() {
        return this.tier >= 13;
    }

    @Override
    @NotNull
    protected StructureCheckResult scanStructure(@NotNull Level level) {
        BlockPos firstCoilPosition = this.toWorldPos(0, 0, 1);
        if (!level.isLoaded(firstCoilPosition)) {
            return this.invalidStructure(unloadedBlock(firstCoilPosition));
        }
        if (!(level.getBlockState(firstCoilPosition).getBlock() instanceof ICAReactorCoil)) {
            return this.invalidStructure(Component.translatable("message.clayium.ca_reactor.no_near_coil"));
        }

        Set<BlockPos> coilPositions = this.findCoilRing(level, firstCoilPosition);
        if (coilPositions == null) {
            return StructureCheckResult.invalid();
        }

        for (BlockPos coilPosition : coilPositions) {
            BlockState coilState = level.getBlockState(coilPosition);
            ICAReactorCoil coil = (ICAReactorCoil) coilState.getBlock();
            if (coil.getCAReactorTier(coilState, level, coilPosition) < this.tier) {
                return this.invalidStructure(positionMessage(
                        "message.clayium.ca_reactor.insufficient_tier_coil", coilPosition));
            }
        }

        Set<BlockPos> coverPositions = new LinkedHashSet<>();
        for (BlockPos coilPosition : coilPositions) {
            for (Direction direction : Direction.values()) {
                coverPositions.add(coilPosition.relative(direction));
            }
        }

        int hullCount = 0;
        int hullRankIndexSum = 0;
        List<BlockPos> glowPositions = new ArrayList<>();
        glowPositions.add(this.worldPosition.immutable());
        List<BlockPos> interfacePositions = new ArrayList<>();
        int maximumHullRank = this.getMaximumHullRank();

        for (BlockPos coverPosition : coverPositions) {
            if (coverPosition.equals(this.worldPosition) || coilPositions.contains(coverPosition)) {
                continue;
            }
            if (!level.isLoaded(coverPosition)) {
                return this.invalidStructure(unloadedBlock(coverPosition));
            }

            BlockState coverState = level.getBlockState(coverPosition);
            if (coverState.getBlock() instanceof ICAReactorHull hull) {
                int rank = hull.getCAReactorRank(coverState, level, coverPosition);
                if (rank < 1 || rank > maximumHullRank) {
                    return this.invalidStructure(positionMessage(
                            "message.clayium.ca_reactor.too_high_tier_hull", coverPosition));
                }
                hullCount++;
                hullRankIndexSum += rank - 1;
                glowPositions.add(coverPosition.immutable());
                continue;
            }

            BlockEntity blockEntity = coverState.hasBlockEntity() ? level.getBlockEntity(coverPosition) : null;
            if (blockEntity instanceof ISynchronizedInterface && blockEntity instanceof ClayContainerBlockEntity part) {
                if (part.tier < this.tier) {
                    return this.invalidStructure(positionMessage(
                            "message.clayium.ca_reactor.insufficient_tier_interface", coverPosition));
                }
                interfacePositions.add(coverPosition.immutable());
                glowPositions.add(coverPosition.immutable());
                continue;
            }

            return this.invalidStructure(positionMessage("message.clayium.ca_reactor.invalid_hull", coverPosition));
        }

        if (hullCount < REQUIRED_HULLS) {
            return this.invalidStructure(Component.translatable(
                    "message.clayium.ca_reactor.invalid_reactor_size", hullCount, REQUIRED_HULLS));
        }

        double rankIndex = (double) hullRankIndexSum / hullCount;
        double efficiency = EFFICIENCY * Math.pow(EFFICIENCY_BASE_PER_HULL, hullCount) *
                Math.pow(EFFICIENCY_BASE_PER_RANK, rankIndex);
        double energyMultiplier = Math.pow(ENERGY_BASE, hullCount * rankIndex);
        this.applyStructureState(rankIndex, hullCount, efficiency, energyMultiplier, glowPositions, Component.empty());
        return new StructureCheckResult(true, this.tier, interfacePositions);
    }

    @Nullable
    private Set<BlockPos> findCoilRing(@NotNull Level level, @NotNull BlockPos firstCoilPosition) {
        Set<BlockPos> walked = new LinkedHashSet<>();
        Queue<BlockPos> pending = new ArrayDeque<>();
        pending.add(firstCoilPosition);

        while (!pending.isEmpty()) {
            BlockPos coilPosition = pending.remove();
            if (!walked.add(coilPosition)) {
                continue;
            }
            if (walked.size() > MAX_COILS) {
                this.invalidStructure(Component.translatable(
                        "message.clayium.ca_reactor.too_many_coils", MAX_COILS));
                return null;
            }

            List<BlockPos> adjacentCoils = new ArrayList<>(2);
            for (BlockPos offset : COIL_NEIGHBOR_OFFSETS) {
                BlockPos adjacentPosition = coilPosition.offset(offset);
                if (!level.isLoaded(adjacentPosition)) {
                    this.invalidStructure(unloadedBlock(adjacentPosition));
                    return null;
                }
                if (level.getBlockState(adjacentPosition).getBlock() instanceof ICAReactorCoil) {
                    adjacentCoils.add(adjacentPosition);
                }
            }

            if (adjacentCoils.size() != 2) {
                this.invalidStructure(positionMessage("message.clayium.ca_reactor.invalid_coil", coilPosition));
                return null;
            }
            pending.addAll(adjacentCoils);
        }

        return walked;
    }

    private int getMaximumHullRank() {
        return switch (this.tier) {
            case 10 -> 2;
            case 11 -> 6;
            case 12, 13 -> 10;
            default -> 0;
        };
    }

    private StructureCheckResult invalidStructure(@NotNull Component error) {
        this.applyStructureState(0.0D, 0, 0.0D, 1.0D, List.of(), error);
        return StructureCheckResult.invalid();
    }

    private void applyStructureState(double rankIndex, int hullCount, double efficiency, double energyMultiplier,
                                     @NotNull List<BlockPos> glowPositions, @NotNull Component error) {
        List<BlockPos> immutableGlowPositions = List.copyOf(glowPositions);
        boolean renderDataChanged = Double.compare(this.reactorRankIndex, rankIndex) != 0 ||
                !this.reactorGlowPositions.equals(immutableGlowPositions);
        this.reactorRankIndex = rankIndex;
        this.reactorHullCount = hullCount;
        this.reactorEfficiency = efficiency;
        this.cePerTickMultiplier = energyMultiplier;
        this.reactorGlowPositions = immutableGlowPositions;
        this.structureError = error;
        if (renderDataChanged) {
            this.refreshReactorRenderData();
            this.structureSyncDirty = true;
        }
    }

    private void refreshReactorRenderData() {
        AABB bounds = new AABB(this.worldPosition);
        for (BlockPos glowPosition : this.reactorGlowPositions) {
            bounds = bounds.minmax(new AABB(glowPosition));
        }
        this.reactorRenderBoundingBox = bounds.inflate(MAXIMUM_REACTOR_GLOW_INFLATE);
        this.reactorRenderRevision++;
    }

    private static List<BlockPos> createCoilNeighborOffsets() {
        List<BlockPos> offsets = new ArrayList<>(18);
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                for (int zz = -1; zz <= 1; zz++) {
                    int nonZeroAxes = Math.abs(xx) + Math.abs(yy) + Math.abs(zz);
                    if (nonZeroAxes == 1 || nonZeroAxes == 2) {
                        offsets.add(new BlockPos(xx, yy, zz));
                    }
                }
            }
        }
        return List.copyOf(offsets);
    }

    private static Component positionMessage(String key, BlockPos pos) {
        return Component.translatable(key, pos.getX(), pos.getY(), pos.getZ());
    }

    private static Component unloadedBlock(BlockPos pos) {
        return positionMessage("message.clayium.ca_reactor.unloaded_block", pos);
    }

    public int getPureAntimatterRankIndex() {
        return Math.min((int) this.reactorRankIndex, 8);
    }

    @NotNull
    public ItemStack getResultPureAntimatter() {
        return switch (this.getPureAntimatterRankIndex()) {
            case 0 -> ClayiumItems.PURE_ANTIMATTER_MATTER.toStack();
            case 1 -> ClayiumItems.COMPRESSED_PURE_ANTIMATTER_1.toStack();
            case 2 -> ClayiumItems.COMPRESSED_PURE_ANTIMATTER_2.toStack();
            case 3 -> ClayiumItems.COMPRESSED_PURE_ANTIMATTER_3.toStack();
            case 4 -> ClayiumItems.COMPRESSED_PURE_ANTIMATTER_4.toStack();
            case 5 -> ClayiumItems.COMPRESSED_PURE_ANTIMATTER_5.toStack();
            case 6 -> ClayiumItems.COMPRESSED_PURE_ANTIMATTER_6.toStack();
            case 7 -> ClayiumItems.COMPRESSED_PURE_ANTIMATTER_7.toStack();
            default -> ClayiumItems.COMPRESSED_PURE_ANTIMATTER_8.toStack();
        };
    }

    public double getReactorRank() {
        return this.reactorHullCount == 0 ? 0.0D : this.reactorRankIndex + 1.0D;
    }

    public int getReactorHullCount() {
        return this.reactorHullCount;
    }

    public double getEfficiency() {
        return this.reactorEfficiency;
    }

    public double getCEPerTickMultiplier() {
        return this.cePerTickMultiplier;
    }

    public boolean isReactorWorking() {
        return this.reactorWorking;
    }

    @NotNull
    public List<BlockPos> getReactorGlowPositions() {
        return this.reactorGlowPositions;
    }

    @NotNull
    public AABB getReactorRenderBoundingBox() {
        return this.reactorRenderBoundingBox;
    }

    public int getReactorRenderRevision() {
        return this.reactorRenderRevision;
    }

    public void applyClientWorkingState(boolean working) {
        if (this.level != null && this.level.isClientSide()) {
            this.reactorWorking = working;
        }
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        super.createMainUI(holder, root);
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(
                () -> Component.translatable(this.isStructureFormed() ?
                        "gui.clayium.ca_reactor.constructed" : "gui.clayium.ca_reactor.invalid"))
                .build()));
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(
                () -> Component.translatable("gui.clayium.ca_reactor.rank_size",
                        String.format(Locale.ROOT, "%.2g", this.getReactorRank()), this.reactorHullCount))
                .build()));
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(
                () -> Component.translatable("gui.clayium.ca_reactor.efficiency",
                        String.format(Locale.ROOT, "%.6g", this.reactorEfficiency)))
                .build()));
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(() -> this.structureError).build()));
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putDouble(REACTOR_RANK_TAG, this.reactorRankIndex);
        tag.putInt(REACTOR_HULL_COUNT_TAG, this.reactorHullCount);
        tag.putDouble(REACTOR_EFFICIENCY_TAG, this.reactorEfficiency);
        tag.putBoolean(REACTOR_WORKING_TAG, this.reactorWorking);
        long[] packedGlowPositions = this.reactorGlowPositions.stream().mapToLong(BlockPos::asLong).toArray();
        tag.putLongArray(REACTOR_GLOW_POSITIONS_TAG, packedGlowPositions);
        return tag;
    }

    @Override
    protected void onReceivePacket(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        if (tag.contains(REACTOR_RANK_TAG)) {
            double rankIndex = tag.getDouble(REACTOR_RANK_TAG);
            List<BlockPos> glowPositions = java.util.Arrays
                    .stream(tag.getLongArray(REACTOR_GLOW_POSITIONS_TAG))
                    .mapToObj(BlockPos::of)
                    .toList();
            boolean renderDataChanged = Double.compare(this.reactorRankIndex, rankIndex) != 0 ||
                    !this.reactorGlowPositions.equals(glowPositions);
            this.reactorRankIndex = rankIndex;
            this.reactorHullCount = tag.getInt(REACTOR_HULL_COUNT_TAG);
            this.reactorEfficiency = tag.getDouble(REACTOR_EFFICIENCY_TAG);
            this.reactorWorking = tag.getBoolean(REACTOR_WORKING_TAG);
            this.reactorGlowPositions = glowPositions;
            if (renderDataChanged) {
                this.refreshReactorRenderData();
            }
        }
        super.onReceivePacket(tag, provider);
    }
}
