package net.kogepan.clayium.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DroppableItemStackHandler extends ItemStackHandler {

    public DroppableItemStackHandler() {
        super();
    }

    public DroppableItemStackHandler(int size) {
        super(size);
    }

    public DroppableItemStackHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, this.stacks);
    }
}
