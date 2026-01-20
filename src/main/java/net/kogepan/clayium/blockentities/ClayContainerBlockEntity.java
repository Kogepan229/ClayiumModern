package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blocks.TestClayContainerBlock;
import net.kogepan.clayium.inventory.MachineIOInventoryWrapper;
import net.kogepan.clayium.utils.MachineIOMode;
import net.kogepan.clayium.utils.MachineIOModes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ClayContainerBlockEntity extends BlockEntity {

    @Getter
    protected final MachineIOModes inputModes = new MachineIOModes();
    @Getter
    protected final MachineIOModes outputModes = new MachineIOModes();

    public ClayContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @NotNull
    public MachineIOMode getInputMode(@NotNull Direction direction) {
        return this.inputModes.getMode(direction);
    }

    @NotNull
    public MachineIOMode getOutputMode(@NotNull Direction direction) {
        return this.outputModes.getMode(direction);
    }

    public MachineIOMode cycleInputMode(@NotNull Direction direction) {
        MachineIOMode current = this.inputModes.getMode(direction);
        MachineIOMode next;
        if (current == MachineIOMode.NONE) {
            next = MachineIOMode.FIRST;
        } else {
            next = MachineIOMode.NONE;
        }

        this.inputModes.setMode(direction, next);
        if (level != null && level.isClientSide()) {
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_IMMEDIATE);
        }

        return next;
    }

    public MachineIOMode cycleOutputMode(@NotNull Direction direction) {
        MachineIOMode current = this.outputModes.getMode(direction);
        MachineIOMode next;
        if (current == MachineIOMode.NONE) {
            next = MachineIOMode.FIRST;
        } else {
            next = MachineIOMode.NONE;
        }

        this.outputModes.setMode(direction, next);
        if (level != null && level.isClientSide()) {
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_IMMEDIATE);
        }

        return next;
    }

    protected abstract IItemHandlerModifiable getInputInventory();

    protected abstract IItemHandlerModifiable getOutputInventory();

    @Nullable
    public IItemHandler getExposedItemHandler(@Nullable Direction side) {
        if (side == null) {
            return null;
        } else {
            IItemHandler inputInventory = switch (this.inputModes.getMode(side)) {
                case FIRST -> new RangedWrapper(this.getInputInventory(), 0, 1);
                case SECOND -> new RangedWrapper(this.getInputInventory(), 1, 2);
                case ALL -> this.getInputInventory();
                case CE -> null;
                default -> null;
            };

            IItemHandler outputInventory = switch (this.outputModes.getMode(side)) {
                case FIRST -> new RangedWrapper(this.getOutputInventory(), 0, 1);
                case SECOND -> new RangedWrapper(this.getOutputInventory(), 1, 2);
                case ALL -> this.getOutputInventory();
                default -> null;
            };

            return new MachineIOInventoryWrapper(inputInventory, outputInventory);
        }
    }

    public boolean canConnectTo(Direction direction) {
        if (this.level == null) return false;

        BlockPos target = this.getBlockPos().relative(direction);

        BlockEntity be = level.getBlockEntity(target);
        if (be != null) {
            return this.level.getCapability(Capabilities.ItemHandler.BLOCK, target, direction.getOpposite()) != null;
        }

        return false;
    }

    public void updatePipeConnections() {
        if (this.level == null) return;

        BlockState state = this.getBlockState();
        if (!state.getValue(TestClayContainerBlock.PIPE)) {
            return;
        }

        for (Direction direction : Direction.values()) {
            state = state.setValue(
                    TestClayContainerBlock.getProperty(direction),
                    this.canConnectTo(direction));
        }

        level.setBlock(this.getBlockPos(), state, Block.UPDATE_IMMEDIATE);
        level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(),
                Block.UPDATE_ALL);
    }
}
