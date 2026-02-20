package net.kogepan.clayium.laser;

import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.IClayLaserAcceptor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Laser propagation engine.
 * <p>
 * Supports straight-line propagation, air/glass pass-through, max length limit,
 * acceptor switch notifications, stop notifications, and block-irradiation branching.
 */
public class ClayLaserIrradiator {

    private static final int DEFAULT_MAX_LASER_LENGTH = 32;
    private static final LaserBlockIrradiationHandler NO_OP_BLOCK_IRRADIATION = (level, targetPos, targetState,
                                                                                 tickEnergy, totalEnergyIrradiated,
                                                                                 irradiationTicks,
                                                                                 onBlockConverted) -> {};

    private final BlockEntity owner;
    private final int maxLaserLength;
    private final LaserBlockIrradiationHandler blockIrradiationHandler;

    @Nullable
    private WeakReference<BlockEntity> previousTarget = null;
    private Direction lastDirection = Direction.NORTH;

    @Nullable
    private BlockPos previousBlockTargetPos = null;
    private long totalEnergyIrradiated = 0L;
    private int irradiationTicks = 0;

    public ClayLaserIrradiator(@NotNull BlockEntity owner) {
        this(owner, DEFAULT_MAX_LASER_LENGTH, NO_OP_BLOCK_IRRADIATION);
    }

    public ClayLaserIrradiator(
                               @NotNull BlockEntity owner,
                               int maxLaserLength,
                               @Nullable LaserBlockIrradiationHandler blockIrradiationHandler) {
        this.owner = owner;
        this.maxLaserLength = Math.max(1, maxLaserLength);
        this.blockIrradiationHandler = blockIrradiationHandler != null ? blockIrradiationHandler :
                NO_OP_BLOCK_IRRADIATION;
    }

    /**
     * Propagates a laser and returns the current laser length.
     */
    public int irradiateLaser(@NotNull Direction direction, @NotNull Laser laser) {
        Level level = this.owner.getLevel();
        if (level == null || level.isClientSide()) {
            return 0;
        }

        int length = this.getLaserLength(level, direction);
        BlockPos targetPos = this.owner.getBlockPos().relative(direction, length);
        Direction targetSide = direction.getOpposite();

        BlockEntity newTargetBlockEntity = level.getBlockEntity(targetPos);
        IClayLaserAcceptor newTargetAcceptor = level.getCapability(
                ClayiumCapabilities.CLAY_LASER_ACCEPTOR,
                targetPos,
                targetSide);

        BlockEntity previousTargetBlockEntity = this.previousTarget != null ? this.previousTarget.get() : null;
        if (newTargetAcceptor != null) {
            newTargetAcceptor.acceptLaser(targetSide, laser);
        }

        if (previousTargetBlockEntity != null && previousTargetBlockEntity != newTargetBlockEntity) {
            Direction previousTargetSide = this.lastDirection.getOpposite();
            IClayLaserAcceptor previousAcceptor = level.getCapability(
                    ClayiumCapabilities.CLAY_LASER_ACCEPTOR,
                    previousTargetBlockEntity.getBlockPos(),
                    previousTargetSide);
            if (previousAcceptor != null) {
                previousAcceptor.acceptLaser(previousTargetSide, null);
            }
        }

        this.previousTarget = newTargetBlockEntity != null ? new WeakReference<>(newTargetBlockEntity) : null;
        this.lastDirection = direction;

        if (newTargetBlockEntity == null) {
            this.irradiateLaserBlock(level, targetPos, laser.energy());
        } else {
            this.resetBlockIrradiationState();
        }
        return length;
    }

    /**
     * Sends a stop notification to the previously irradiated target.
     */
    public void stopIrradiation() {
        Level level = this.owner.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockEntity previousTargetBlockEntity = this.previousTarget != null ? this.previousTarget.get() : null;
        if (previousTargetBlockEntity != null) {
            Direction targetSide = this.lastDirection.getOpposite();
            IClayLaserAcceptor previousAcceptor = level.getCapability(
                    ClayiumCapabilities.CLAY_LASER_ACCEPTOR,
                    previousTargetBlockEntity.getBlockPos(),
                    targetSide);
            if (previousAcceptor != null) {
                previousAcceptor.acceptLaser(targetSide, null);
            }
        }

        this.previousTarget = null;
        this.resetBlockIrradiationState();
    }

    public long getTotalEnergyIrradiated() {
        return this.totalEnergyIrradiated;
    }

    public int getIrradiationTicks() {
        return this.irradiationTicks;
    }

    private int getLaserLength(@NotNull Level level, @NotNull Direction direction) {
        BlockPos.MutableBlockPos cursor = this.owner.getBlockPos().mutable();
        for (int i = 1; i <= this.maxLaserLength; i++) {
            cursor.move(direction);
            if (canGoThroughBlock(level.getBlockState(cursor))) {
                continue;
            }
            return i;
        }
        return this.maxLaserLength;
    }

    private static boolean canGoThroughBlock(@NotNull BlockState state) {
        if (state.isAir()) {
            return true;
        }

        return state.getBlock() instanceof TransparentBlock || state.getBlock() instanceof StainedGlassPaneBlock ||
                state.is(Blocks.GLASS_PANE);
    }

    private void irradiateLaserBlock(@NotNull Level level, @NotNull BlockPos targetPos, long energy) {
        if (!Objects.equals(this.previousBlockTargetPos, targetPos)) {
            this.totalEnergyIrradiated = 0L;
            this.irradiationTicks = 0;
            this.previousBlockTargetPos = targetPos.immutable();
        }

        this.totalEnergyIrradiated += energy;
        this.irradiationTicks++;
        this.blockIrradiationHandler.irradiate(
                level,
                targetPos,
                level.getBlockState(targetPos),
                energy,
                this.totalEnergyIrradiated,
                this.irradiationTicks,
                this::resetBlockIrradiationState);
    }

    private void resetBlockIrradiationState() {
        this.previousBlockTargetPos = null;
        this.totalEnergyIrradiated = 0L;
        this.irradiationTicks = 0;
    }
}
