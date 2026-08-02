package net.kogepan.clayium.recipes.display;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.SizedIngredientSlotDisplay;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumSlotDisplays {

    public static final DeferredRegister<SlotDisplay.Type<?>> SLOT_DISPLAYS = DeferredRegister
            .create(Registries.SLOT_DISPLAY, Clayium.MODID);

    public static final DeferredHolder<SlotDisplay.Type<?>, SlotDisplay.Type<SizedIngredientSlotDisplay>> SIZED_INGREDIENT = SLOT_DISPLAYS
            .register("sized_ingredient", () -> SizedIngredientSlotDisplay.TYPE);

    private ClayiumSlotDisplays() {}
}
