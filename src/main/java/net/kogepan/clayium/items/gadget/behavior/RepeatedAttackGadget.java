package net.kogepan.clayium.items.gadget.behavior;

import net.kogepan.clayium.api.gadget.ClayGadgetCategories;
import net.kogepan.clayium.api.gadget.IClayGadget;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

/** Marker behavior for the repeatedly-attack event handler. */
public final class RepeatedAttackGadget implements IClayGadget {

    @Override
    @NotNull
    public ResourceLocation getCategory(@NotNull ItemStack stack) {
        return ClayGadgetCategories.REPEATEDLY_ATTACK;
    }
}
