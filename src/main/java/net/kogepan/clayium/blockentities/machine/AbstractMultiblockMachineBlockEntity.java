package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.AbstractRecipeLogic;
import net.kogepan.clayium.blockentities.trait.MultiblockRecipeLogic;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.ISynchronizedInterface;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractMultiblockMachineBlockEntity extends WorkableClayContainerBlockEntity {

    public static final String FRONT_OVERLAY_VARIANT_FORMED = "formed";

    private static final int STRUCTURE_CHECK_INTERVAL = 20;
    private static final String FRONT_OVERLAY_VARIANT_DEFAULT = "default";
    private static final String STRUCTURE_FORMED_TAG = "structureFormed";

    private final Set<BlockPos> linkedInterfacePositions = new HashSet<>();

    private boolean structureFormed;
    private int structureRecipeTier;
    private int structureCheckTimer;

    protected AbstractMultiblockMachineBlockEntity(
                                                   @NotNull BlockEntityType<?> type,
                                                   @NotNull BlockPos pos,
                                                   @NotNull BlockState blockState,
                                                   @NotNull List<MachineIOMode> validInputModes,
                                                   @NotNull List<MachineIOMode> validOutputModes,
                                                   int inputSize,
                                                   int outputSize,
                                                   Function<WorkableClayContainerBlockEntity, @NotNull AbstractRecipeLogic> recipeLogicProvider,
                                                   @NotNull RecipeType<?> recipeType) {
        super(type, pos, blockState, validInputModes, validOutputModes, inputSize, outputSize, recipeLogicProvider,
                recipeType);
    }

    public final boolean isStructureFormed() {
        return this.structureFormed;
    }

    public final int getStructureRecipeTier() {
        return this.structureRecipeTier;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide()) {
            this.verifyStructure(false);
        }
    }

    @Override
    protected void tick() {
        if (this.level != null && !this.level.isClientSide()) {
            this.structureCheckTimer++;
            if (this.structureCheckTimer >= STRUCTURE_CHECK_INTERVAL) {
                this.structureCheckTimer = 0;
                this.verifyStructure(true);
            }
        }
        super.tick();
    }

    @Override
    public void initDefaultRoutes() {}

    private void verifyStructure(boolean notifyStateChange) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        StructureCheckResult result = this.scanStructure(currentLevel);
        boolean previousFormed = this.structureFormed;
        this.structureFormed = result.valid();
        this.structureRecipeTier = result.recipeTier();

        boolean recipeProgressInvalidated = false;
        if (this.structureFormed) {
            this.autoLinkInterfaces(result.interfacePositions());
        } else {
            this.clearMultiblockLinksToThisController();
            recipeProgressInvalidated = this.invalidateRecipeProgress();
        }

        if (recipeProgressInvalidated) {
            this.setChanged();
        }
        if (notifyStateChange && previousFormed != this.structureFormed) {
            currentLevel.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_CLIENTS);
        }
    }

    private void autoLinkInterfaces(@NotNull List<BlockPos> interfacePositions) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        GlobalPos controllerTarget = GlobalPos.of(currentLevel.dimension(), this.worldPosition);
        Set<BlockPos> desiredPositions = Set.copyOf(interfacePositions);
        Iterator<BlockPos> linkedPositions = this.linkedInterfacePositions.iterator();
        while (linkedPositions.hasNext()) {
            BlockPos linkedPosition = linkedPositions.next();
            if (!desiredPositions.contains(linkedPosition)) {
                this.clearMultiblockLink(linkedPosition, controllerTarget);
                linkedPositions.remove();
            }
        }

        for (BlockPos interfacePos : interfacePositions) {
            ISynchronizedInterface synchronizedInterface = this.getLoadedSynchronizedInterface(interfacePos);
            if (synchronizedInterface != null && synchronizedInterface.setLinkedTarget(
                    controllerTarget, ISynchronizedInterface.LinkSource.MULTIBLOCK)) {
                this.linkedInterfacePositions.add(interfacePos.immutable());
            }
        }
    }

    private void clearMultiblockLinksToThisController() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        GlobalPos controllerTarget = GlobalPos.of(currentLevel.dimension(), this.worldPosition);
        for (BlockPos linkedPosition : this.linkedInterfacePositions) {
            this.clearMultiblockLink(linkedPosition, controllerTarget);
        }
        this.linkedInterfacePositions.clear();
    }

    private void clearMultiblockLink(@NotNull BlockPos interfacePos, @NotNull GlobalPos controllerTarget) {
        ISynchronizedInterface synchronizedInterface = this.getLoadedSynchronizedInterface(interfacePos);
        if (synchronizedInterface != null &&
                synchronizedInterface.getLinkSource() == ISynchronizedInterface.LinkSource.MULTIBLOCK &&
                controllerTarget.equals(synchronizedInterface.getLinkedTargetPos())) {
            synchronizedInterface.clearLinkedTarget();
        }
    }

    @Nullable
    private ISynchronizedInterface getLoadedSynchronizedInterface(@NotNull BlockPos interfacePos) {
        Level currentLevel = this.level;
        if (currentLevel == null || !currentLevel.isLoaded(interfacePos)) {
            return null;
        }
        BlockState state = currentLevel.getBlockState(interfacePos);
        BlockEntity blockEntity = state.hasBlockEntity() ? currentLevel.getBlockEntity(interfacePos) : null;
        return currentLevel.getCapability(ClayiumCapabilities.SYNCHRONIZED_INTERFACE,
                interfacePos, state, blockEntity, null);
    }

    private boolean invalidateRecipeProgress() {
        if (this.recipeLogic instanceof MultiblockRecipeLogic multiblockRecipeLogic) {
            return multiblockRecipeLogic.invalidateProgress();
        }
        return false;
    }

    @NotNull
    protected abstract StructureCheckResult scanStructure(@NotNull Level level);

    @NotNull
    protected final BlockPos toWorldPos(int rightOffset, int upOffset, int backwardsOffset) {
        Direction facing = Direction.NORTH;
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof ClayContainerBlock containerBlock) {
            facing = state.getValue(containerBlock.getFacingProperty());
        }
        Direction right = facing.getClockWise();
        Direction backwards = facing.getOpposite();
        return this.worldPosition
                .relative(right, rightOffset)
                .relative(Direction.UP, upOffset)
                .relative(backwards, backwardsOffset);
    }

    protected static int calculateStructureTier(int weightSum, int partCount) {
        int averageWeight = weightSum / partCount;
        int tier = (int) (16.0D - Math.floor(Math.log(averageWeight) / Math.log(2.0D) + 0.5D));
        return Math.max(tier, 0);
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide()) {
            this.clearMultiblockLinksToThisController();
            this.invalidateRecipeProgress();
        }
        super.setRemoved();
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putBoolean(STRUCTURE_FORMED_TAG, this.structureFormed);
        return tag;
    }

    @Override
    protected void onReceivePacket(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        if (tag.contains(STRUCTURE_FORMED_TAG)) {
            this.structureFormed = tag.getBoolean(STRUCTURE_FORMED_TAG);
        }
        super.onReceivePacket(tag, provider);
    }

    @Override
    @NotNull
    protected String getFrontOverlayVariant() {
        return this.structureFormed ? FRONT_OVERLAY_VARIANT_FORMED : FRONT_OVERLAY_VARIANT_DEFAULT;
    }

    protected record StructureCheckResult(boolean valid, int recipeTier,
                                          @NotNull List<BlockPos> interfacePositions) {

        protected StructureCheckResult {
            interfacePositions = List.copyOf(interfacePositions);
        }

        protected static StructureCheckResult invalid() {
            return new StructureCheckResult(false, 0, List.of());
        }
    }
}
