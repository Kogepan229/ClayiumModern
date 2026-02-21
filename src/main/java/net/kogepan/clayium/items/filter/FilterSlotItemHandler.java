package net.kogepan.clayium.items.filter;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Item handler that backs the filter slots (5x2). Reads/writes from the held ItemStack's filter
 * slots data component. Phantom-style: items are stored for filter matching only; count is always
 * 1 when stored.
 */
public class FilterSlotItemHandler implements IItemHandlerModifiable {

    public static final int SLOTS = 10; // 5x2

    private final Supplier<ItemStack> stackSupplier;

    public FilterSlotItemHandler(@NotNull ItemStack stack) {
        this(() -> stack);
    }

    public FilterSlotItemHandler(@NotNull Supplier<ItemStack> stackSupplier) {
        this.stackSupplier = stackSupplier;
    }

    private List<ItemStack> getStacks() {
        List<ItemStack> list = FilterItemHelper.readItems(stackSupplier.get());
        while (list.size() < SLOTS) {
            list.add(ItemStack.EMPTY);
        }
        return list;
    }

    private void setStacks(@NotNull List<ItemStack> stacks) {
        List<ItemStack> normalized = new ArrayList<>(SLOTS);
        for (int i = 0; i < SLOTS; i++) {
            normalized.add(i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
        }
        FilterItemHelper.writeItems(stackSupplier.get(), normalized);
    }

    @Override
    public int getSlots() {
        return SLOTS;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        List<ItemStack> list = getStacks();
        if (slot >= 0 && slot < list.size()) {
            return list.get(slot).copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack s) {
        if (slot < 0 || slot >= SLOTS) return;
        List<ItemStack> list = new ArrayList<>(getStacks());
        while (list.size() < SLOTS) {
            list.add(ItemStack.EMPTY);
        }
        if (s.isEmpty()) {
            list.set(slot, ItemStack.EMPTY);
        } else {
            ItemStack copy = s.copy();
            copy.setCount(1);
            list.set(slot, copy);
        }
        setStacks(list);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack s, boolean simulate) {
        if (s.isEmpty()) return ItemStack.EMPTY;
        if (simulate) {
            return s.copy();
        }
        setStackInSlot(slot, s);
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack in = getStackInSlot(slot);
        if (in.isEmpty()) return ItemStack.EMPTY;
        if (simulate) {
            return in.copyWithCount(Math.min(amount, in.getCount()));
        }
        setStackInSlot(slot, ItemStack.EMPTY);
        return in;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack s) {
        return true;
    }
}
