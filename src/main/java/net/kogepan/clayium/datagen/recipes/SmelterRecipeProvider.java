package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SmelterRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result, long energy,
                               long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.SMELTER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);
        output.accept(Clayium.id("smelter/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off
        // Explicit smelter recipes only (same as Original recipeSmelter.addRecipe).
        // Vanilla furnace recipes will be supported by Smelter in a later implementation.

        // Tier 5: ALUMINIUM, IMPURE_ALUMINIUM (Original also has SODIUM, ZINC, NICKEL, ALUMINIUM_OD - not in ClayiumModern)
        create(recipeOutput, "aluminium_dust", ClayiumItems.ALUMINIUM_DUST.toStack(),
                ClayiumItems.ALUMINIUM_INGOT.toStack(), CEUtils.ONE_MILLI_CE, 200, 5);
        create(recipeOutput, "impure_aluminium_dust", ClayiumItems.IMPURE_ALUMINIUM_DUST.toStack(),
                ClayiumItems.IMPURE_ALUMINIUM_INGOT.toStack(), CEUtils.ONE_MILLI_CE, 200, 5);

        // Tier 6: AZ91D_ALLOY, ZK60A_ALLOY (Original also has MAGNESIUM, LITHIUM, ZIRCONIUM, ZINCALMINIUM_ALLOY, ZINCONIUM_ALLOY - not in ClayiumModern)
        create(recipeOutput, "az91d_alloy_dust", ClayiumItems.AZ91D_ALLOY_DUST.toStack(),
                ClayiumItems.AZ91D_ALLOY_INGOT.toStack(), CEUtils.ONE_MILLI_CE * 2, 400, 6);
        create(recipeOutput, "zk60a_alloy_dust", ClayiumItems.ZK60A_ALLOY_DUST.toStack(),
                ClayiumItems.ZK60A_ALLOY_INGOT.toStack(), CEUtils.ONE_MILLI_CE * 2, 400, 6);

        // spotless:on
    }
}
