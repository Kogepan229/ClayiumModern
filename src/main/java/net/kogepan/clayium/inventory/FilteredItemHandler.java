package net.kogepan.clayium.inventory;

import net.kogepan.clayium.capability.IItemFilter;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Wraps an IItemHandler and applies an IItemFilter to insert, extract, and isItemValid.
 * Only stacks that pass the filter are allowed through.
 */
public class FilteredItemHandler implements IItemHandler {

    private final IItemHandler delegate;
    private final IItemFilter filter;

    public FilteredItemHandler(@NotNull IItemHandler delegate, @NotNull IItemFilter filter) {
        this.delegate = delegate;
        this.filter = filter;
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (filter.test(stack)) {
            return delegate.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack wouldExtract = delegate.extractItem(slot, amount, true);
        if (filter.test(wouldExtract)) {
            return delegate.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return filter.test(stack) && delegate.isItemValid(slot, stack);
    }
}
