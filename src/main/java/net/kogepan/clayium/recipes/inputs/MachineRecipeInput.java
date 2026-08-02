package net.kogepan.clayium.recipes.inputs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public final class MachineRecipeInput implements RecipeInput {

    private final List<ItemStack> items;

    public MachineRecipeInput(List<ItemStack> items) {
        this.items = List.copyOf(items);
    }

    @Override
    public ItemStack getItem(int index) {
        if (index < 0 || index >= this.items.size()) {
            return ItemStack.EMPTY;
        }
        return this.items.get(index);
    }

    @Override
    public int size() {
        return this.items.size();
    }
}
