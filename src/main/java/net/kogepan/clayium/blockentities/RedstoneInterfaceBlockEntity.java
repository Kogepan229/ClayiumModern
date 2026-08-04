package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.IExternalControl;
import net.kogepan.clayium.capability.ISynchronizedInterface;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Block entity for Redstone Interface signal conversion and machine control. */
public class RedstoneInterfaceBlockEntity extends ClayContainerBlockEntity implements ISynchronizedInterface {

    private static final IItemHandlerModifiable EMPTY_ITEM_HANDLER = new ItemStackHandler(0);

    private static final String LINKED_TARGET_TAG = "linkedTarget";
    private static final String LINKED_TARGET_PRESENT_TAG = "hasLinkedTarget";
    private static final String LINK_SOURCE_TAG = "linkSource";
    private static final String MODE_TAG = "mode";
    private static final String LAST_INPUT_POWERED_TAG = "lastInputPowered";
    private static final String FORCE_INPUT_EVALUATION_TAG = "forceInputEvaluation";
    private static final String OUTPUT_POWER_TAG = "outputPower";

    @Nullable
    private GlobalPos linkedTargetPos;
    @NotNull
    private LinkSource linkSource = LinkSource.NONE;
    @NotNull
    private Mode mode = Mode.NONE;
    private boolean lastInputPowered;
    private boolean forceInputEvaluation;
    private int outputPower;

    public RedstoneInterfaceBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ClayiumBlockEntityTypes.REDSTONE_INTERFACE_BLOCK_ENTITY.get(), pos, state,
                List.of(MachineIOMode.NONE), List.of(MachineIOMode.NONE));
    }

    @Override
    protected void tick() {
        super.tick();
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        IExternalControl control = this.resolveExternalControl(serverLevel);
        boolean inputPowered = serverLevel.hasNeighborSignal(this.worldPosition);
        this.applyInputControl(control, inputPowered);
        this.setOutputPower(this.calculateOutputPower(control));
        this.lastInputPowered = inputPowered;
        this.forceInputEvaluation = false;
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return EMPTY_ITEM_HANDLER;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return EMPTY_ITEM_HANDLER;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {}

    @Nullable
    @Override
    public GlobalPos getLinkedTargetPos() {
        return this.linkedTargetPos;
    }

    @NotNull
    @Override
    public LinkSource getLinkSource() {
        return this.linkSource;
    }

    @Override
    public boolean canSynchronize() {
        return true;
    }

    @Override
    public void setCanSynchronize(boolean canSynchronize) {}

    @Override
    public boolean setLinkedTarget(@NotNull GlobalPos target, @NotNull LinkSource source) {
        Level currentLevel = this.level;
        if (currentLevel != null && target.dimension().equals(currentLevel.dimension()) &&
                target.pos().equals(this.worldPosition)) {
            return false;
        }
        if (currentLevel instanceof ServerLevel serverLevel) {
            ServerLevel targetLevel = serverLevel.getServer().getLevel(target.dimension());
            if (targetLevel != null && targetLevel.isLoaded(target.pos()) &&
                    this.resolveExternalControl(serverLevel, target) == null) {
                return false;
            }
        }

        boolean changed = !target.equals(this.linkedTargetPos) || this.linkSource != source;
        this.linkedTargetPos = target;
        this.linkSource = source;
        if (changed) {
            this.markInterfaceChanged();
        }
        return true;
    }

    @Override
    public void clearLinkedTarget() {
        if (this.linkedTargetPos == null && this.linkSource == LinkSource.NONE) {
            return;
        }
        this.linkedTargetPos = null;
        this.linkSource = LinkSource.NONE;
        this.setOutputPower(0);
        this.markInterfaceChanged();
    }

    @NotNull
    public Mode getMode() {
        return this.mode;
    }

    @NotNull
    public Mode cycleMode() {
        Mode[] modes = Mode.values();
        this.mode = modes[(this.mode.ordinal() + 1) % modes.length];
        this.forceInputEvaluation = true;
        this.markInterfaceChanged();
        return this.mode;
    }

    public int getOutputPower() {
        return this.outputPower;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString(MODE_TAG, this.mode.name());
        tag.putBoolean(LAST_INPUT_POWERED_TAG, this.lastInputPowered);
        tag.putBoolean(FORCE_INPUT_EVALUATION_TAG, this.forceInputEvaluation);
        tag.putInt(OUTPUT_POWER_TAG, this.outputPower);
        if (this.linkSource == LinkSource.MULTIBLOCK) {
            tag.putBoolean(LINKED_TARGET_PRESENT_TAG, false);
        } else {
            this.writeLinkedTarget(tag, provider, true);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        this.mode = Mode.fromName(tag.getString(MODE_TAG));
        this.lastInputPowered = tag.getBoolean(LAST_INPUT_POWERED_TAG);
        this.forceInputEvaluation = tag.getBoolean(FORCE_INPUT_EVALUATION_TAG);
        this.outputPower = tag.getInt(OUTPUT_POWER_TAG);
        this.readLinkedTarget(tag, provider, true);
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putString(MODE_TAG, this.mode.name());
        tag.putInt(OUTPUT_POWER_TAG, this.outputPower);
        this.writeLinkedTarget(tag, provider, false);
        return tag;
    }

    @Override
    protected void onReceivePacket(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        if (tag.contains(MODE_TAG)) {
            this.mode = Mode.fromName(tag.getString(MODE_TAG));
        }
        if (tag.contains(OUTPUT_POWER_TAG)) {
            this.outputPower = tag.getInt(OUTPUT_POWER_TAG);
        }
        if (tag.contains(LINKED_TARGET_PRESENT_TAG) || tag.contains(LINKED_TARGET_TAG)) {
            this.readLinkedTarget(tag, provider, false);
        }
        super.onReceivePacket(tag, provider);
    }

    @Nullable
    private IExternalControl resolveExternalControl(@NotNull ServerLevel serverLevel) {
        if (this.linkedTargetPos == null) {
            return null;
        }
        return this.resolveExternalControl(serverLevel, this.linkedTargetPos);
    }

    @Nullable
    private IExternalControl resolveExternalControl(@NotNull ServerLevel serverLevel, @NotNull GlobalPos target) {
        MinecraftServer server = serverLevel.getServer();
        ServerLevel targetLevel = server.getLevel(target.dimension());
        BlockPos targetPos = target.pos();
        if (targetLevel == null || !targetLevel.isLoaded(targetPos)) {
            return null;
        }

        BlockState targetState = targetLevel.getBlockState(targetPos);
        BlockEntity targetBlockEntity = targetState.hasBlockEntity() ? targetLevel.getBlockEntity(targetPos) : null;
        return targetLevel.getCapability(ClayiumCapabilities.EXTERNAL_CONTROL,
                targetPos, targetState, targetBlockEntity, null);
    }

    private void applyInputControl(@Nullable IExternalControl control, boolean inputPowered) {
        if (control == null || !this.forceInputEvaluation && inputPowered == this.lastInputPowered) {
            return;
        }

        switch (this.mode) {
            case DO_WORK -> {
                if (inputPowered) {
                    control.startWork();
                } else {
                    control.stopWork();
                }
            }
            case DO_NOT_WORK -> {
                if (inputPowered) {
                    control.stopWork();
                } else {
                    control.startWork();
                }
            }
            case START_WORK -> {
                if (inputPowered) {
                    control.startWork();
                }
            }
            case STOP_WORK -> {
                if (inputPowered) {
                    control.stopWork();
                }
            }
            case DO_WORK_ONCE -> {
                if (inputPowered) {
                    control.doWorkOnce();
                }
            }
            default -> {}
        }
    }

    private int calculateOutputPower(@Nullable IExternalControl control) {
        if (control == null) {
            return 0;
        }
        return switch (this.mode) {
            case EMIT_IF_IDLE -> control.isDoingWork() ? 0 : 15;
            case EMIT_IF_WORK_SCHEDULED -> control.isScheduled() ? 15 : 0;
            case EMIT_IF_DOING_WORK -> control.isDoingWork() ? 15 : 0;
            default -> 0;
        };
    }

    private void setOutputPower(int power) {
        int clampedPower = Math.clamp(power, 0, 15);
        if (this.outputPower == clampedPower) {
            return;
        }
        this.outputPower = clampedPower;
        this.setChanged();
        Level currentLevel = this.level;
        if (currentLevel != null && !currentLevel.isClientSide()) {
            currentLevel.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
            currentLevel.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_CLIENTS);
        }
    }

    private void markInterfaceChanged() {
        this.setChanged();
        Level currentLevel = this.level;
        if (currentLevel != null && !currentLevel.isClientSide()) {
            currentLevel.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_CLIENTS);
        }
    }

    private void writeLinkedTarget(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider,
                                   boolean includeLinkSource) {
        tag.putBoolean(LINKED_TARGET_PRESENT_TAG, this.linkedTargetPos != null);
        if (this.linkedTargetPos == null) {
            return;
        }
        GlobalPos.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.linkedTargetPos)
                .result()
                .ifPresent(encoded -> tag.put(LINKED_TARGET_TAG, encoded));
        if (includeLinkSource) {
            tag.putString(LINK_SOURCE_TAG, this.linkSource.name());
        }
    }

    private void readLinkedTarget(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider,
                                  boolean includeLinkSource) {
        this.linkedTargetPos = null;
        if (includeLinkSource) {
            this.linkSource = LinkSource.NONE;
        }

        boolean hasTarget = tag.contains(LINKED_TARGET_PRESENT_TAG) ? tag.getBoolean(LINKED_TARGET_PRESENT_TAG) :
                tag.contains(LINKED_TARGET_TAG);
        if (!hasTarget || !tag.contains(LINKED_TARGET_TAG)) {
            if (includeLinkSource) {
                this.linkSource = LinkSource.NONE;
            }
            return;
        }
        this.linkedTargetPos = GlobalPos.CODEC
                .parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(LINKED_TARGET_TAG))
                .result()
                .orElse(null);
        if (includeLinkSource && this.linkedTargetPos != null) {
            this.linkSource = readLinkSource(tag.getString(LINK_SOURCE_TAG));
        }
    }

    @NotNull
    private static LinkSource readLinkSource(@NotNull String name) {
        if (name.isEmpty()) {
            return LinkSource.MANUAL;
        }
        try {
            return LinkSource.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return LinkSource.MANUAL;
        }
    }

    /** Original Redstone Interface operating modes in right-click cycle order. */
    public enum Mode {

        NONE("none"),
        EMIT_IF_IDLE("emit_if_idle"),
        EMIT_IF_WORK_SCHEDULED("emit_if_work_scheduled"),
        EMIT_IF_DOING_WORK("emit_if_doing_work"),
        DO_WORK("do_work"),
        DO_NOT_WORK("do_not_work"),
        START_WORK("start_work"),
        STOP_WORK("stop_work"),
        DO_WORK_ONCE("do_work_once");

        private final String translationSuffix;

        Mode(String translationSuffix) {
            this.translationSuffix = translationSuffix;
        }

        @NotNull
        public Component getDisplayName() {
            return Component.translatable("gui.clayium.redstone_interface.mode." + this.translationSuffix);
        }

        @NotNull
        private static Mode fromName(@NotNull String name) {
            try {
                return Mode.valueOf(name);
            } catch (IllegalArgumentException ignored) {
                return NONE;
            }
        }
    }
}
