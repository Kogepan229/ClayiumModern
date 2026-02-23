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

public class ClayBlastFurnaceRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.CLAY_BLAST_FURNACE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);
        output.accept(Clayium.id("clay_blast_furnace/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 6
        create(recipeOutput, "clay_steel_dust", ClayiumItems.CLAY_STEEL_DUST.toStack(), ClayiumItems.CLAY_STEEL_INGOT.toStack(), CEUtils.ONE_MILLI_CE * 100, 500, 6);
        create(recipeOutput, "az91d_alloy_dust", ClayiumItems.AZ91D_ALLOY_DUST.toStack(), ClayiumItems.AZ91D_ALLOY_INGOT.toStack(), CEUtils.ONE_MILLI_CE * 2, 400, 6);
        create(recipeOutput, "zk60a_alloy_dust", ClayiumItems.ZK60A_ALLOY_DUST.toStack(), ClayiumItems.ZK60A_ALLOY_INGOT.toStack(), CEUtils.ONE_MILLI_CE * 2, 400, 6);

        // Tier 7
        create(recipeOutput, "clayium_dust", ClayiumItems.CLAYIUM_DUST.toStack(), ClayiumItems.CLAYIUM_INGOT.toStack(), CEUtils.ceToLong(2), 1000, 7);

        // Tier 8
        create(recipeOutput, "ultimate_alloy_dust", ClayiumItems.ULTIMATE_ALLOY_DUST.toStack(), ClayiumItems.ULTIMATE_ALLOY_INGOT.toStack(), CEUtils.ceToLong(40), 2000, 8);

        // spotless:on
    }
}
