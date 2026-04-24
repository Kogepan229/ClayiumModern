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

public class ElectrolysisReactorRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result, long energy,
                               long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.ELECTROLYSIS_REACTOR_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);
        output.accept(Clayium.id("electrolysis_reactor/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 6
        create(recipeOutput, "aluminium_dust", ClayiumItems.IMPURE_ALUMINIUM_DUST.toStack(), ClayiumItems.ALUMINIUM_DUST.toStack(), CEUtils.milliCeToLong(100), 100, 6);
        create(recipeOutput, "magnesium_dust", ClayiumItems.IMPURE_MAGNESIUM_DUST.toStack(), ClayiumItems.MAGNESIUM_DUST.toStack(), CEUtils.milliCeToLong(100), 100, 6);
        create(recipeOutput, "sodium_dust", ClayiumItems.IMPURE_SODIUM_DUST.toStack(), ClayiumItems.SODIUM_DUST.toStack(), CEUtils.milliCeToLong(100), 100, 6);
        create(recipeOutput, "lithium_dust", ClayiumItems.IMPURE_LITHIUM_DUST.toStack(), ClayiumItems.LITHIUM_DUST.toStack(), CEUtils.milliCeToLong(100), 100, 6);
        create(recipeOutput, "zirconium_dust", ClayiumItems.IMPURE_ZIRCONIUM_DUST.toStack(), ClayiumItems.ZIRCONIUM_DUST.toStack(), CEUtils.milliCeToLong(100), 100, 6);
        create(recipeOutput, "zinc_dust", ClayiumItems.IMPURE_ZINC_DUST.toStack(), ClayiumItems.ZINC_DUST.toStack(), CEUtils.milliCeToLong(100), 100, 6);

        // Tier 7
        create(recipeOutput, "manganese_dust", ClayiumItems.IMPURE_MANGANESE_DUST.toStack(), ClayiumItems.MANGANESE_DUST.toStack(), CEUtils.ceToLong(10), 300, 7);
        create(recipeOutput, "potassium_dust", ClayiumItems.IMPURE_POTASSIUM_DUST.toStack(), ClayiumItems.POTASSIUM_DUST.toStack(), CEUtils.ceToLong(10), 300, 7);
        create(recipeOutput, "hafnium_dust", ClayiumItems.IMPURE_HAFNIUM_DUST.toStack(), ClayiumItems.HAFNIUM_DUST.toStack(), CEUtils.ceToLong(10), 300, 7);
        create(recipeOutput, "strontium_dust", ClayiumItems.IMPURE_STRONTIUM_DUST.toStack(), ClayiumItems.STRONTIUM_DUST.toStack(), CEUtils.ceToLong(10), 300, 7);
        create(recipeOutput, "barium_dust", ClayiumItems.IMPURE_BARIUM_DUST.toStack(), ClayiumItems.BARIUM_DUST.toStack(), CEUtils.ceToLong(10), 300, 7);
        create(recipeOutput, "calcium_dust", ClayiumItems.IMPURE_CALCIUM_DUST.toStack(), ClayiumItems.CALCIUM_DUST.toStack(), CEUtils.ceToLong(10), 300, 7);

        // Tier 8
        create(recipeOutput, "iron_dust", ClayiumItems.IMPURE_IRON_DUST.toStack(), ClayiumItems.IRON_DUST.toStack(), CEUtils.ceToLong(100), 1000, 8);
        create(recipeOutput, "lead_dust", ClayiumItems.IMPURE_LEAD_DUST.toStack(), ClayiumItems.LEAD_DUST.toStack(), CEUtils.ceToLong(100), 1000, 8);
        create(recipeOutput, "copper_dust", ClayiumItems.IMPURE_COPPER_DUST.toStack(), ClayiumItems.COPPER_DUST.toStack(), CEUtils.ceToLong(100), 1000, 8);

        // Tier 9
        create(recipeOutput, "nickel_dust", ClayiumItems.IMPURE_NICKEL_DUST.toStack(), ClayiumItems.NICKEL_DUST.toStack(), CEUtils.ceToLong(1000), 3000, 9);
        create(recipeOutput, "beryllium_dust", ClayiumItems.IMPURE_BERYLLIUM_DUST.toStack(), ClayiumItems.BERYLLIUM_DUST.toStack(), CEUtils.ceToLong(1000), 3000, 9);
        create(recipeOutput, "chrome_dust", ClayiumItems.IMPURE_CHROME_DUST.toStack(), ClayiumItems.CHROME_DUST.toStack(), CEUtils.ceToLong(1000), 3000, 9);
        create(recipeOutput, "titanium_dust", ClayiumItems.IMPURE_TITANIUM_DUST.toStack(), ClayiumItems.TITANIUM_DUST.toStack(), CEUtils.ceToLong(1000), 3000, 9);

        // spotless:on
    }
}
