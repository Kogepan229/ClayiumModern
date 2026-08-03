package net.kogepan.clayium.api.gadget;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

/**
 * Behavior exposed by an item that can be installed in a Clay Gadget Holder.
 * <p>
 * Add-ons attach implementations through {@code ClayiumCapabilities.CLAY_GADGET} during
 * {@code RegisterCapabilitiesEvent}. Callbacks run on both logical sides; implementations must
 * select the side appropriate for their state changes.
 */
public interface IClayGadget {

    /** Returns the mutually exclusive category of this gadget. */
    @NotNull
    ResourceLocation getCategory(@NotNull ItemStack stack);

    /** Returns the priority used when multiple holders contain the same category. */
    default int getPriority(@NotNull ItemStack stack) {
        return 0;
    }

    /** Called after this gadget becomes the resolved gadget for its category. */
    default void onActivated(@NotNull ClayGadgetContext context) {}

    /** Called before this gadget stops being the resolved gadget for its category. */
    default void onDeactivated(@NotNull ClayGadgetContext context) {}

    /** Called once per player tick while this gadget is active. */
    default void tick(@NotNull ClayGadgetContext context) {}
}
