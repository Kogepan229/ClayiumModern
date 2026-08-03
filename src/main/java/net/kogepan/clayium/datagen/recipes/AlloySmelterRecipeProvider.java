package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class AlloySmelterRecipeProvider {

    private AlloySmelterRecipeProvider() {}

    private static void create(RecipeOutput output, String name, Ingredient ingredient1, int amount1,
                               Ingredient ingredient2, int amount2, ItemStack result, long energy, long duration,
                               int tier) {
        MachineRecipe recipe = new MachineRecipe(ClayiumRecipeTypes.ALLOY_SMELTER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient1, amount1), ItemIngredientStack.of(ingredient2, amount2)),
                List.of(result), duration, energy, tier);
        output.accept(Clayium.id("alloy_smelter/" + name), recipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput output) {
        // spotless:off

        // Tier 6
        create(output, "zincalminium_alloy_ingot", Ingredient.of(ClayiumItems.ZINC_INGOT.get()), 9, Ingredient.of(ClayiumItems.ALUMINIUM_INGOT.get()), 1, ClayiumItems.ZINCALMINIUM_ALLOY_INGOT.toStack(10), CEUtils.milliCeToLong(100), 50, 6);
        create(output, "az91d_alloy_ingot", Ingredient.of(ClayiumItems.MAGNESIUM_INGOT.get()), 9, Ingredient.of(ClayiumItems.ZINCALMINIUM_ALLOY_INGOT.get()), 1, ClayiumItems.AZ91D_ALLOY_INGOT.toStack(10), CEUtils.ONE_CE, 500, 6);
        create(output, "zinconium_alloy_ingot", Ingredient.of(ClayiumItems.ZINC_INGOT.get()), 9, Ingredient.of(ClayiumItems.ZIRCONIUM_INGOT.get()), 1, ClayiumItems.ZINCONIUM_ALLOY_INGOT.toStack(10), CEUtils.ceToLong(3), 50, 6);
        create(output, "zk60a_alloy_ingot", Ingredient.of(ClayiumItems.MAGNESIUM_INGOT.get()), 19, Ingredient.of(ClayiumItems.ZINCONIUM_ALLOY_INGOT.get()), 1, ClayiumItems.ZK60A_ALLOY_INGOT.toStack(20), CEUtils.ceToLong(3), 500, 6);

        // spotless:on
    }
}
