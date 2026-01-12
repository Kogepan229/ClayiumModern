package net.kogepan.clayium.integration.emi;

import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;

import java.util.function.Function;

@EmiEntrypoint
public class ClayiumEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(EMIClayWorkTableRecipe.CATEGORY);
        registry.addWorkstation(EMIClayWorkTableRecipe.CATEGORY, EmiStack.of(ClayiumBlocks.CLAY_WORK_TABLE));
        adaptRecipeType(registry, ClayiumRecipeTypes.CLAY_WORK_TABLE_RECIPE_TYPE.get(), EMIClayWorkTableRecipe::new);
    }

    private static <C extends RecipeInput, T extends Recipe<C>> void adaptRecipeType(EmiRegistry registry,
                                                                                     RecipeType<T> recipeType,
                                                                                     Function<RecipeHolder<T>, ? extends EmiRecipe> adapter) {
        registry.getRecipeManager().getAllRecipesFor(recipeType)
                .stream()
                .map(adapter)
                .forEach(registry::addRecipe);
    }
}
