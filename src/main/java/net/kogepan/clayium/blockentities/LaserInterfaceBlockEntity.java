package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.IClayLaserAcceptor;
import net.kogepan.clayium.capability.ISynchronizedInterface;
import net.kogepan.clayium.laser.Laser;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Receives lasers from any side and forwards their merged value to a synchronized target. */
public class LaserInterfaceBlockEntity extends ClayContainerBlockEntity
                                       implements IClayLaserAcceptor, ISynchronizedInterface {

    private static final String LINKED_TARGET_TAG = "linkedTarget";
    private static final String LINKED_TARGET_PRESENT_TAG = "hasLinkedTarget";
    private static final String LINK_SOURCE_TAG = "linkSource";
    private static final String CAN_SYNCHRONIZE_TAG = "canSynchronize";
    private static final IItemHandlerModifiable EMPTY_INVENTORY = new ItemStackHandler(0);

    private final Map<Direction, Laser> receivedLasers = new EnumMap<>(Direction.class);

    @Nullable
    private GlobalPos linkedTargetPos;
    @NotNull
    private LinkSource linkSource = LinkSource.NONE;
    private boolean canSynchronize;
    @Nullable
    private GlobalPos lastForwardedTarget;
    @Nullable
    private Direction lastForwardedSide;

    public LaserInterfaceBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.LASER_INTERFACE_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE), List.of(MachineIOMode.NONE));
    }

    @Override
    protected void tick() {
        if (this.level instanceof ServerLevel) {
            this.forwardMergedLaser();
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

    private void forwardMergedLaser() {
        Laser mergedLaser = Laser.merge(this.receivedLasers.values());
        this.receivedLasers.clear();
        GlobalPos target = this.linkedTargetPos;
        Direction targetSide = this.getTargetSide();

        if (this.lastForwardedTarget != null &&
                (mergedLaser == null || !Objects.equals(this.lastForwardedTarget, target) ||
                        this.lastForwardedSide != targetSide)) {
            this.notifyTarget(this.lastForwardedTarget, this.lastForwardedSide, null);
            this.lastForwardedTarget = null;
            this.lastForwardedSide = null;
        }

        if (mergedLaser == null || target == null) {
            return;
        }
        if (this.notifyTarget(target, targetSide, mergedLaser)) {
            this.lastForwardedTarget = target;
            this.lastForwardedSide = targetSide;
        }
    }

    private boolean notifyTarget(@NotNull GlobalPos target, @Nullable Direction targetSide, @Nullable Laser laser) {
        if (!(this.level instanceof ServerLevel sourceLevel) || targetSide == null) {
            return false;
        }
        ServerLevel targetLevel = sourceLevel.getServer().getLevel(target.dimension());
        if (targetLevel == null || !targetLevel.isLoaded(target.pos())) {
            return false;
        }
        IClayLaserAcceptor acceptor = targetLevel.getCapability(
                ClayiumCapabilities.CLAY_LASER_ACCEPTOR, target.pos(), targetSide);
        if (acceptor == null || acceptor == this) {
            return false;
        }
        acceptor.acceptLaser(targetSide, laser);
        return true;
    }

    @NotNull
    private Direction getTargetSide() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof net.kogepan.clayium.blocks.ClayContainerBlock containerBlock) {
            return state.getValue(containerBlock.getFacingProperty()).getOpposite();
        }
        return Direction.SOUTH;
    }

    private void stopForwarding() {
        if (this.lastForwardedTarget != null) {
            this.notifyTarget(this.lastForwardedTarget, this.lastForwardedSide, null);
        }
        this.lastForwardedTarget = null;
        this.lastForwardedSide = null;
    }

    @Override
    public void setRemoved() {
        this.stopForwarding();
        super.setRemoved();
    }

    @Override
    public @NotNull IItemHandlerModifiable getInputInventory() {
        return EMPTY_INVENTORY;
    }

    @Override
    public @NotNull IItemHandlerModifiable getOutputInventory() {
        return EMPTY_INVENTORY;
    }

    @Override
    @NotNull
    protected List<IItemHandler> getInventoryHandlersForDrops() {
        return List.of();
    }

    @Override
    public boolean acceptsClayInterfaceSynchronization() {
        return false;
    }

    @Override
    public @Nullable GlobalPos getLinkedTargetPos() {
        return this.linkedTargetPos;
    }

    @Override
    public @NotNull LinkSource getLinkSource() {
        return this.linkSource;
    }

    @Override
    public boolean canSynchronize() {
        return this.canSynchronize;
    }

    @Override
    public void setCanSynchronize(boolean canSynchronize) {
        if (this.canSynchronize == canSynchronize) {
            return;
        }
        this.canSynchronize = canSynchronize;
        this.setChanged();
    }

    @Override
    public boolean setLinkedTarget(@NotNull GlobalPos target, @NotNull LinkSource source) {
        Level level = this.level;
        boolean selfTarget = level != null && target.dimension().equals(level.dimension()) &&
                target.pos().equals(this.worldPosition);
        if (source == LinkSource.MANUAL && !this.canSynchronize || selfTarget) {
            return false;
        }
        if (!target.equals(this.linkedTargetPos) || source != this.linkSource) {
            this.stopForwarding();
            this.linkedTargetPos = target;
            this.linkSource = source;
            this.setChanged();
        }
        return true;
    }

    @Override
    public void clearLinkedTarget() {
        if (this.linkedTargetPos == null && this.linkSource == LinkSource.NONE) {
            return;
        }
        this.stopForwarding();
        this.linkedTargetPos = null;
        this.linkSource = LinkSource.NONE;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean(CAN_SYNCHRONIZE_TAG, this.canSynchronize);
        boolean persistLinkedTarget = this.linkedTargetPos != null && this.linkSource != LinkSource.MULTIBLOCK;
        tag.putBoolean(LINKED_TARGET_PRESENT_TAG, persistLinkedTarget);
        if (persistLinkedTarget) {
            GlobalPos.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.linkedTargetPos)
                    .result()
                    .ifPresent(encoded -> tag.put(LINKED_TARGET_TAG, encoded));
            tag.putString(LINK_SOURCE_TAG, this.linkSource.name());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        this.canSynchronize = tag.getBoolean(CAN_SYNCHRONIZE_TAG);
        this.linkedTargetPos = null;
        this.linkSource = LinkSource.NONE;
        if (tag.getBoolean(LINKED_TARGET_PRESENT_TAG) && tag.contains(LINKED_TARGET_TAG)) {
            this.linkedTargetPos = GlobalPos.CODEC
                    .parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(LINKED_TARGET_TAG))
                    .result()
                    .orElse(null);
            if (this.linkedTargetPos != null) {
                try {
                    this.linkSource = LinkSource.valueOf(tag.getString(LINK_SOURCE_TAG));
                } catch (IllegalArgumentException ignored) {
                    this.linkSource = LinkSource.MANUAL;
                }
            }
        }
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {}
}
