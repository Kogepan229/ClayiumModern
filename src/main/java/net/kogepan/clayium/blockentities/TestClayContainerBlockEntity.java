package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blocks.TestClayContainerBlock;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import org.jetbrains.annotations.Nullable;

public class TestClayContainerBlockEntity extends ClayContainerBlockEntity {

    ItemStackHandler inputInventory = new ItemStackHandler(1);
    ItemStackHandler outputInventory = new ItemStackHandler(1);

    public TestClayContainerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClayiumBlockEntityTypes.TEST_CLAY_CONTAINER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void onPlacedByServer(@Nullable LivingEntity placer, ItemStack stack) {
        this.inputModes.setMode(Direction.UP, MachineIOMode.FIRST);
        this.inputModes.setMode(this.getBlockState().getValue(TestClayContainerBlock.FACING).getOpposite(),
                MachineIOMode.CE);
        this.outputModes.setMode(Direction.DOWN, MachineIOMode.FIRST);

        super.onPlacedByServer(placer, stack);
    }

    @Override
    protected IItemHandlerModifiable getInputInventory() {
        return inputInventory;
    }

    @Override
    protected IItemHandlerModifiable getOutputInventory() {
        return outputInventory;
    }
}
