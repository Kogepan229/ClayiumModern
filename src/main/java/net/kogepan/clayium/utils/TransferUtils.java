package net.kogepan.clayium.utils;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class TransferUtils {

    public static boolean simulateInsertItemsToHandler(IItemHandlerModifiable handler, List<ItemStack> stacks) {
        List<ItemStack> copiedStacks = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            copiedStacks.add(stack.copy());
        }

        ItemStackHandler copiedHandler = new ItemStackHandler(handler.getSlots());
        for (int i = 0; i < handler.getSlots(); i++) {
            copiedHandler.setStackInSlot(i, handler.getStackInSlot(i).copy());
        }

        for (ItemStack stack : copiedStacks) {
            ItemStack remain = ItemHandlerHelper.insertItemStacked(copiedHandler, stack, false);
            if (!remain.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
