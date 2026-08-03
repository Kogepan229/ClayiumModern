package net.kogepan.clayium.items.gadget;

import net.kogepan.clayium.api.gadget.IClayGadget;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.registries.ClayiumDataComponents;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;

import org.jetbrains.annotations.NotNull;

public class ClayGadgetItemHandler extends ComponentItemHandler {

    public static final int SLOTS = 10;

    public ClayGadgetItemHandler(@NotNull ItemStack holder) {
        super(holder, ClayiumDataComponents.GADGET_HOLDER_CONTENTS.get(), SLOTS);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        super.setStackInSlot(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        if (stack.is(ClayiumItems.CLAY_GADGET_HOLDER)) {
            return false;
        }
        IClayGadget gadget = stack.getCapability(ClayiumCapabilities.CLAY_GADGET);
        if (gadget == null) {
            return false;
        }
        ResourceLocation category = gadget.getCategory(stack);
        for (int index = 0; index < getSlots(); index++) {
            if (index == slot) {
                continue;
            }
            ItemStack installed = getStackInSlot(index);
            IClayGadget installedGadget = installed.getCapability(ClayiumCapabilities.CLAY_GADGET);
            if (installedGadget != null && category.equals(installedGadget.getCategory(installed))) {
                return false;
            }
        }
        return true;
    }
}
