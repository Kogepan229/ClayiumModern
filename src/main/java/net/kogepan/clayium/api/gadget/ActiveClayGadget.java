package net.kogepan.clayium.api.gadget;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

/** Read-only view of a resolved active gadget. */
public record ActiveClayGadget(@NotNull ResourceLocation category, int priority,
                               @NotNull ItemStack stack, @NotNull IClayGadget gadget) {

    public ActiveClayGadget {
        stack = stack.copy();
    }

    @Override
    @NotNull
    public ItemStack stack() {
        return stack.copy();
    }
}
