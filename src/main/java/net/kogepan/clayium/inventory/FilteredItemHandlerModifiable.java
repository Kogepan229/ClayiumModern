package net.kogepan.clayium.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * A filtered item handler that only allows items matching a predicate.
 */
public class FilteredItemHandlerModifiable implements IItemHandlerModifiable {

    private final IItemHandlerModifiable delegate;
    private final Predicate<ItemStack> filter;

    public FilteredItemHandlerModifiable(@NotNull IItemHandlerModifiable delegate,
                                         @NotNull Predicate<ItemStack> filter) {
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
        if (!filter.test(stack)) {
            return stack;
        }
        return delegate.insertItem(slot, stack, simulate);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return delegate.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return filter.test(stack) && delegate.isItemValid(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        delegate.setStackInSlot(slot, stack);
    }
}
