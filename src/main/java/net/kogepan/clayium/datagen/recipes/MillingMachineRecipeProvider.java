package net.kogepan.clayium.datagen.recipes;

import net.minecraft.data.recipes.RecipeOutput;

import org.jetbrains.annotations.NotNull;

/**
 * Milling Machine recipe provider.
 * <p>
 * Original (CRecipes) defines: Dense Clay PLATE -> ClayCircuitBoard (time 32);
 * IND_CLAY PLATE -> ClayCircuitBoard (time 1); ADVIND_CLAY PLATE (tier 3) ->
 * CEEBoard (energy 2L, time 32). ClayiumModern does not yet register
 * ClayCircuitBoard or CEEBoard items. Add create() calls here once those
 * items are registered.
 */
public class MillingMachineRecipeProvider {

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // No recipes until ClayCircuitBoard and CEEBoard items are registered.
    }
}
