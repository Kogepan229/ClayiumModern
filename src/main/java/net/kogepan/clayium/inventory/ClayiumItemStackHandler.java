package net.kogepan.clayium.inventory;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ClayiumItemStackHandler extends ItemStackHandler {

    private final ClayContainerBlockEntity blockEntity;

    public ClayiumItemStackHandler(ClayContainerBlockEntity blockEntity) {
        super(1);
        this.blockEntity = blockEntity;
    }

    public ClayiumItemStackHandler(ClayContainerBlockEntity blockEntity, int size) {
        super(size);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        this.blockEntity.setChanged();
    }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, this.stacks);
    }
}
