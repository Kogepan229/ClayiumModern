package net.kogepan.clayium.recipes.inputs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * RecipeInput implementation for machine recipes.
 * Wraps a list of ItemStacks to provide input items for recipe matching.
 */
public class MachineRecipeInput implements RecipeInput {

    private final List<ItemStack> items;

    public MachineRecipeInput(@NotNull List<ItemStack> items) {
        this.items = items;
    }

    @Override
    @NotNull
    public ItemStack getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }
}
