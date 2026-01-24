package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.inputs.MachineRecipeInput;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record MachineRecipe(
                            @NotNull SimpleMachineRecipeType<MachineRecipe> recipeType,
                            @NotNull List<ItemIngredientStack> inputs,
                            @NotNull List<ItemStack> outputs,
                            long duration,
                            long cePerTick,
                            int recipeTier)
        implements Recipe<MachineRecipeInput> {

    /**
     * Checks if this recipe matches the given input and machine tier.
     *
     * @param input The recipe input containing item stacks
     * @param level The level (not used in machine recipes)
     * @return true if the recipe matches
     */
    @Override
    public boolean matches(@NotNull MachineRecipeInput input, @NotNull Level level) {
        return matches(input, Integer.MAX_VALUE);
    }

    /**
     * Checks if this recipe matches the given input and machine tier.
     *
     * @param input       The recipe input containing item stacks
     * @param machineTier The tier of the machine
     * @return true if the recipe matches
     */
    public boolean matches(@NotNull MachineRecipeInput input, int machineTier) {
        if (this.recipeTier > machineTier) {
            return false;
        }

        List<ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            inputItems.add(input.getItem(i));
        }
        return matchesItems(inputItems);
    }

    /**
     * Checks if the input items match the recipe requirements.
     * This implementation follows the original Recipe class logic where ingredients
     * are matched in order, and each ingredient must match a different input slot.
     *
     * @param inputItems The recipe input containing item stacks
     * @return true if all ingredients match
     */
    public boolean matchesItems(@NotNull List<ItemStack> inputItems) {
        for (ItemIngredientStack ingredient : this.inputs) {
            boolean isMatched = false;
            for (int i = 0; i < inputItems.size(); i++) {
                ItemStack itemStack = inputItems.get(i);

                // Check if this slot matches the ingredient (both item type and amount)
                if (ingredient.test(itemStack) && itemStack.getCount() >= ingredient.getAmount()) {
                    isMatched = true;
                    break;
                }
            }
            // If one of the ingredients is not matched, return false
            if (!isMatched) {
                return false;
            }
        }
        // All ingredients are matched
        return true;
    }

    /**
     * Assembles the recipe result.
     *
     * @param input    The recipe input
     * @param provider The holder lookup provider
     * @return The first output item stack
     */
    @Override
    @NotNull
    public ItemStack assemble(@NotNull MachineRecipeInput input, @NotNull HolderLookup.Provider provider) {
        if (outputs.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return outputs.getFirst().copy();
    }

    /**
     * Checks if the recipe can be crafted in the given dimensions.
     * Machine recipes don't have dimension constraints.
     *
     * @param width  The width (not used)
     * @param height The height (not used)
     * @return true
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    /**
     * Gets the result item stack.
     *
     * @param provider The holder lookup provider
     * @return The first output item stack
     */
    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        if (outputs.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return outputs.getFirst();
    }

    /**
     * Gets all output item stacks.
     *
     * @return A copy of all output item stacks
     */
    @NotNull
    public List<ItemStack> copyOutputs() {
        return outputs.stream().map(ItemStack::copy).toList();
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ClayiumRecipeSerializers.MACHINE_RECIPE_SERIALIZER.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return recipeType;
    }
}
