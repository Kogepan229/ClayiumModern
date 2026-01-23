package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;

import java.util.List;

public class TestClayContainerBlockEntity extends ClayContainerBlockEntity {

    ItemStackHandler inputInventory = new ItemStackHandler(1);
    ItemStackHandler outputInventory = new ItemStackHandler(1);

    public TestClayContainerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClayiumBlockEntityTypes.TEST_CLAY_CONTAINER_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE), List.of(MachineIOMode.NONE));
    }

    @Override
    protected IItemHandlerModifiable getInputInventory() {
        return inputInventory;
    }

    @Override
    protected IItemHandlerModifiable getOutputInventory() {
        return outputInventory;
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        return null;
    }
}
