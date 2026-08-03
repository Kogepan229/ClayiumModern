package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.ClayInterfaceBlockEntity;
import net.kogepan.clayium.blockentities.LaserInterfaceBlockEntity;
import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.ClayReactorRecipeLogic;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.IClayLaserAcceptor;
import net.kogepan.clayium.capability.ISynchronizedInterface;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.laser.Laser;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Clay Reactor controller and its 3 by 3 by 3 multiblock validation. */
public class ClayReactorBlockEntity extends WorkableClayContainerBlockEntity implements IClayLaserAcceptor {

    private static final int STRUCTURE_CHECK_INTERVAL = 20;
    private static final int STRUCTURE_PARTS = 26;
    private static final String FRONT_OVERLAY_VARIANT_DEFAULT = "default";
    public static final String FRONT_OVERLAY_VARIANT_FORMED = "formed";

    private final Map<Direction, Laser> receivedLasers = new EnumMap<>(Direction.class);
    private final Set<BlockPos> linkedInterfacePositions = new HashSet<>();

    @Getter
    private boolean structureFormed;
    @Getter
    private int structureRecipeTier;
    private int structureCheckTimer;
    private long laserEnergy;

    public ClayReactorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CLAY_REACTOR_BLOCK_ENTITY.get(), pos, blockState,
                getValidInputModes(2),
                getValidOutputModes(2),
                2,
                2,
                be -> new ClayReactorRecipeLogic(
                        be,
                        ClayiumRecipeTypes.CLAY_REACTOR_RECIPE_TYPE.get(),
                        be.getEnergyHolder(),
                        () -> ((ClayReactorBlockEntity) be).isStructureFormed(),
                        () -> ((ClayReactorBlockEntity) be).getStructureRecipeTier(),
                        () -> ((ClayReactorBlockEntity) be).getLaserEnergy()),
                ClayiumRecipeTypes.CLAY_REACTOR_RECIPE_TYPE.get());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide()) {
            this.verifyStructure();
        }
    }

    @Override
    protected void tick() {
        if (this.level != null && !this.level.isClientSide()) {
            this.updateLaserInput();
            this.structureCheckTimer++;
            if (this.structureCheckTimer >= STRUCTURE_CHECK_INTERVAL) {
                this.structureCheckTimer = 0;
                this.verifyStructure();
            }
        }
        super.tick();
    }

    @Override
    public void initDefaultRoutes() {}

    @Override
    public void acceptLaser(@NotNull Direction irradiatedSide, @Nullable Laser laser) {
        if (laser == null) {
            this.receivedLasers.remove(irradiatedSide);
        } else {
            this.receivedLasers.put(irradiatedSide, laser);
        }
    }

    public long getLaserEnergy() {
        return this.laserEnergy;
    }

    private void updateLaserInput() {
        Laser mergedLaser = Laser.merge(this.receivedLasers.values());
        this.laserEnergy = mergedLaser == null ? 0L : mergedLaser.energy();
        this.receivedLasers.clear();
    }

    private void verifyStructure() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        StructureCheckResult result = this.scanStructure(currentLevel);
        boolean previousFormed = this.structureFormed;
        int previousTier = this.structureRecipeTier;
        this.structureFormed = result.valid();
        this.structureRecipeTier = result.recipeTier();

        if (this.structureFormed) {
            this.autoLinkInterfaces(result.interfacePositions());
        } else {
            this.clearMultiblockLinksToThisController();
            if (this.recipeLogic instanceof ClayReactorRecipeLogic reactorLogic) {
                reactorLogic.invalidateProgress();
            }
        }

        if (previousFormed != this.structureFormed || previousTier != this.structureRecipeTier) {
            this.setChanged();
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

    @NotNull
    private StructureCheckResult scanStructure(@NotNull Level level) {
        int count = 0;
        int sum = 0;
        List<BlockPos> interfacePositions = new ArrayList<>();

        for (int yy = -1; yy <= 1; yy++) {
            for (int xx = -1; xx <= 1; xx++) {
                for (int zz = 0; zz <= 2; zz++) {
                    if (xx == 0 && yy == 0 && zz == 0) {
                        continue;
                    }
                    count++;
                    boolean laserInterfacePosition = xx == 0 && yy == 1 && zz == 1;
                    BlockPos partPos = this.toWorldPos(xx, yy, zz);
                    if (!level.isLoaded(partPos)) {
                        return StructureCheckResult.invalid();
                    }
                    BlockState state = level.getBlockState(partPos);
                    BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(partPos) : null;
                    int partTier = this.getPartTier(state, blockEntity, laserInterfacePosition);
                    if (partTier < 7) {
                        return StructureCheckResult.invalid();
                    }
                    if (blockEntity instanceof ClayInterfaceBlockEntity ||
                            blockEntity instanceof LaserInterfaceBlockEntity) {
                        interfacePositions.add(partPos.immutable());
                    }
                    sum += (int) Math.pow(2.0D, 16 - partTier);
                }
            }
        }

        if (count != STRUCTURE_PARTS) {
            return StructureCheckResult.invalid();
        }
        return new StructureCheckResult(true, this.calculateRecipeTier(sum, count), interfacePositions);
    }

    private int getPartTier(@NotNull BlockState state, @Nullable BlockEntity blockEntity,
                            boolean laserInterfacePosition) {
        if (laserInterfacePosition) {
            return blockEntity instanceof LaserInterfaceBlockEntity laserInterface &&
                    laserInterface.tier >= 7 && laserInterface.tier <= 13 ? laserInterface.tier : -1;
        }
        if (blockEntity instanceof LaserInterfaceBlockEntity) {
            return -1;
        }
        int hullTier = ClayiumBlocks.getMachineHullTier(state.getBlock());
        if (hullTier >= 7 && hullTier <= 13) {
            return hullTier;
        }
        if (blockEntity instanceof ClayInterfaceBlockEntity clayInterface &&
                clayInterface.tier >= 7 && clayInterface.tier <= 13) {
            return clayInterface.tier;
        }
        return -1;
    }

    private int calculateRecipeTier(int sum, int count) {
        int averageWeight = sum / count;
        int tier = (int) (16.0D - Math.floor(Math.log(averageWeight) / Math.log(2.0D) + 0.5D));
        return Math.max(tier, 0);
    }

    @NotNull
    private BlockPos toWorldPos(int xx, int yy, int zz) {
        Direction facing = Direction.NORTH;
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof net.kogepan.clayium.blocks.ClayContainerBlock containerBlock) {
            facing = state.getValue(containerBlock.getFacingProperty());
        }
        Direction right = facing.getClockWise();
        Direction back = facing.getOpposite();
        return this.worldPosition
                .relative(right, xx)
                .relative(Direction.UP, yy)
                .relative(back, zz);
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide()) {
            this.clearMultiblockLinksToThisController();
            this.receivedLasers.clear();
            this.laserEnergy = 0L;
            if (this.recipeLogic instanceof ClayReactorRecipeLogic reactorLogic) {
                reactorLogic.invalidateProgress();
            }
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("structureFormed", this.structureFormed);
        tag.putInt("structureRecipeTier", this.structureRecipeTier);
        tag.putInt("structureCheckTimer", this.structureCheckTimer);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        this.structureFormed = tag.getBoolean("structureFormed");
        this.structureRecipeTier = tag.getInt("structureRecipeTier");
        this.structureCheckTimer = tag.getInt("structureCheckTimer");
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putBoolean("structureFormed", this.structureFormed);
        return tag;
    }

    @Override
    protected void onReceivePacket(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        if (tag.contains("structureFormed")) {
            this.structureFormed = tag.getBoolean("structureFormed");
        }
        super.onReceivePacket(tag, provider);
    }

    @Override
    @NotNull
    protected String getFrontOverlayVariant() {
        return this.structureFormed ? FRONT_OVERLAY_VARIANT_FORMED : FRONT_OVERLAY_VARIANT_DEFAULT;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        super.createMainUI(holder, root);
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(
                () -> Component.translatable("gui.clayium.clay_reactor.recipe_tier", this.structureRecipeTier))
                .build()));
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(
                () -> Component.translatable("gui.clayium.clay_reactor.laser_energy", this.laserEnergy))
                .build()));
    }

    private record StructureCheckResult(boolean valid, int recipeTier, @NotNull List<BlockPos> interfacePositions) {

        private static StructureCheckResult invalid() {
            return new StructureCheckResult(false, 0, List.of());
        }
    }
}
