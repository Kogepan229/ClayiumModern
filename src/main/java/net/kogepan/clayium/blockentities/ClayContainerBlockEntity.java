package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.inventory.MachineIOInventoryWrapper;
import net.kogepan.clayium.utils.MachineIOMode;
import net.kogepan.clayium.utils.MachineIOModes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
}
