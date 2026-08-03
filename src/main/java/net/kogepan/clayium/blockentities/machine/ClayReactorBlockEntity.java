package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.ClayInterfaceBlockEntity;
import net.kogepan.clayium.blockentities.LaserInterfaceBlockEntity;
import net.kogepan.clayium.blockentities.trait.ClayReactorRecipeLogic;
import net.kogepan.clayium.capability.IClayLaserAcceptor;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.laser.Laser;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Clay Reactor controller and its 3 by 3 by 3 multiblock validation. */
public class ClayReactorBlockEntity extends AbstractMultiblockMachineBlockEntity implements IClayLaserAcceptor {

    private static final int STRUCTURE_PARTS = 26;

    private final Map<Direction, Laser> receivedLasers = new EnumMap<>(Direction.class);

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
    protected void tick() {
        if (this.level != null && !this.level.isClientSide()) {
            this.updateLaserInput();
        }
        super.tick();
    }

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

    @Override
    @NotNull
    protected StructureCheckResult scanStructure(@NotNull Level level) {
        int count = 0;
        int weightSum = 0;
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
                    weightSum += (int) Math.pow(2.0D, 16 - partTier);
                }
            }
        }

        if (count != STRUCTURE_PARTS) {
            return StructureCheckResult.invalid();
        }
        return new StructureCheckResult(true, calculateStructureTier(weightSum, count), interfacePositions);
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

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide()) {
            this.receivedLasers.clear();
            this.laserEnergy = 0L;
        }
        super.setRemoved();
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        super.createMainUI(holder, root);
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(
                () -> Component.translatable(
                        "gui.clayium.clay_reactor.recipe_tier", this.getStructureRecipeTier()))
                .build()));
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(
                () -> Component.translatable("gui.clayium.clay_reactor.laser_energy", this.laserEnergy))
                .build()));
    }
}
