package net.kogepan.clayium.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MachineIOInventoryWrapper implements IItemHandler {

    @NotNull
    final IItemHandler inputInventory;
    @NotNull
    final IItemHandler outputInventory;

    public MachineIOInventoryWrapper(@Nullable IItemHandler inputInventory, @Nullable IItemHandler outputInventory) {
        this.inputInventory = inputInventory != null ? inputInventory : EmptyItemHandler.INSTANCE;
        this.outputInventory = outputInventory != null ? outputInventory : EmptyItemHandler.INSTANCE;
    }

    @Override
    public int getSlots() {
        return inputInventory.getSlots() + outputInventory.getSlots();
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        if (slot < inputInventory.getSlots()) {
            return inputInventory.getStackInSlot(slot);
        } else {
            return outputInventory.getStackInSlot(slot - inputInventory.getSlots());
        }
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack itemStack, boolean simulate) {
        if (slot < inputInventory.getSlots()) {
            return inputInventory.insertItem(slot, itemStack, simulate);
        } else {
            return itemStack;
        }
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < inputInventory.getSlots()) {
            return ItemStack.EMPTY;
        } else {
            return outputInventory.extractItem(slot - inputInventory.getSlots(), amount, simulate);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot < inputInventory.getSlots()) {
            return inputInventory.getSlotLimit(slot);
        } else {
            return outputInventory.getSlotLimit(slot - inputInventory.getSlots());
        }
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack itemStack) {
        if (slot < inputInventory.getSlots()) {
            return inputInventory.isItemValid(slot, itemStack);
        } else {
            return false;
        }
    }
}
