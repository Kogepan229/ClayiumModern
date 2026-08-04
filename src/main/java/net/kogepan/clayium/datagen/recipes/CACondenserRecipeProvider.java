package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CACondenserRecipeProvider {

    private CACondenserRecipeProvider() {}

    public static void buildRecipes(@NotNull RecipeOutput output) {
        MachineRecipe recipe = new MachineRecipe(ClayiumRecipeTypes.CA_CONDENSER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ClayiumItems.ANTIMATTER_SEED.toStack())),
                List.of(ClayiumItems.ANTIMATTER_MATTER.toStack()), 2_000L, CEUtils.ceToLong(250L), 0);
        output.accept(Clayium.id("ca_condenser/antimatter"), recipe, null);
    }
}
