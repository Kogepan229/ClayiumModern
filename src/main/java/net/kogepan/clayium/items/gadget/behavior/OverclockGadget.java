package net.kogepan.clayium.items.gadget.behavior;

import net.kogepan.clayium.api.gadget.ClayGadgetCategories;
import net.kogepan.clayium.api.gadget.IClayGadget;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

/** Built-in overclock gadget data used by the client tick handler. */
public record OverclockGadget(int delay, int priority) implements IClayGadget {

    @Override
    @NotNull
    public ResourceLocation getCategory(@NotNull ItemStack stack) {
        return ClayGadgetCategories.OVERCLOCK;
    }

    @Override
    public int getPriority(@NotNull ItemStack stack) {
        return priority;
    }
}
