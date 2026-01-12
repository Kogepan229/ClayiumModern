package net.kogepan.clayium.integration.rei;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class REIUtils {

    public static EntryIngredient ofIngredient(Ingredient ingredient, int amount) {
        if (ingredient.isEmpty()) return EntryIngredient.empty();
        ItemStack[] matchingStacks = ingredient.getItems();
        if (matchingStacks.length == 0) return EntryIngredient.empty();
        if (matchingStacks.length == 1) return EntryIngredient.of(EntryStacks.of(matchingStacks[0]));
        EntryIngredient.Builder result = EntryIngredient.builder(matchingStacks.length);
        for (ItemStack matchingStack : matchingStacks) {
            if (!matchingStack.isEmpty()) {
                result.add(EntryStacks.of(matchingStack.copyWithCount(amount)));
            }
        }
        return result.build();
    }
}
