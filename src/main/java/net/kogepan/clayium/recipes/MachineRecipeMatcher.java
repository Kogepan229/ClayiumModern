package net.kogepan.clayium.recipes;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/** Matches machine recipe ingredients to distinct inventory slots. */
public final class MachineRecipeMatcher {

    private MachineRecipeMatcher() {}

    public static boolean matches(@NotNull List<ItemIngredientStack> ingredients,
                                  @NotNull List<ItemStack> inputStacks) {
        return findMatches(ingredients, inputStacks) != null;
    }

    /**
     * Finds a one-to-one ingredient-to-slot assignment while allowing unrelated stacks in other slots.
     *
     * @return an array indexed by ingredient, containing the matched slot index, or {@code null}
     */
    @Nullable
    public static int[] findMatches(@NotNull List<ItemIngredientStack> ingredients,
                                    @NotNull List<ItemStack> inputStacks) {
        if (ingredients.size() > inputStacks.size()) {
            return null;
        }

        int[] matchedSlots = new int[ingredients.size()];
        Arrays.fill(matchedSlots, -1);
        boolean[] usedSlots = new boolean[inputStacks.size()];
        return findMatches(ingredients, inputStacks, matchedSlots, usedSlots, 0) ? matchedSlots : null;
    }

    private static boolean findMatches(@NotNull List<ItemIngredientStack> ingredients,
                                       @NotNull List<ItemStack> inputStacks,
                                       int[] matchedSlots,
                                       boolean[] usedSlots,
                                       int ingredientIndex) {
        if (ingredientIndex >= ingredients.size()) {
            return true;
        }

        ItemIngredientStack ingredient = ingredients.get(ingredientIndex);
        for (int slot = 0; slot < inputStacks.size(); slot++) {
            ItemStack inputStack = inputStacks.get(slot);
            if (usedSlots[slot] || !ingredient.test(inputStack) ||
                    inputStack.getCount() < ingredient.getAmount()) {
                continue;
            }

            usedSlots[slot] = true;
            matchedSlots[ingredientIndex] = slot;
            if (findMatches(ingredients, inputStacks, matchedSlots, usedSlots, ingredientIndex + 1)) {
                return true;
            }
            usedSlots[slot] = false;
            matchedSlots[ingredientIndex] = -1;
        }
        return false;
    }
}
