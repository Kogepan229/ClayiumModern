package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.WeightedItemStack;
import net.kogepan.clayium.recipes.recipes.WeightedMachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ChemicalMetalSeparatorRecipeProvider {

    private ChemicalMetalSeparatorRecipeProvider() {}

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        WeightedMachineRecipe recipe = new WeightedMachineRecipe(
                List.of(ItemIngredientStack.of(ClayiumItems.INDUSTRIAL_CLAY_DUST.toStack())),
                List.of(
                        new WeightedItemStack(ClayiumItems.IMPURE_ALUMINIUM_DUST.toStack(), 200),
                        new WeightedItemStack(ClayiumItems.IMPURE_MANGANESE_DUST.toStack(), 80),
                        new WeightedItemStack(ClayiumItems.IMPURE_MAGNESIUM_DUST.toStack(), 60),
                        new WeightedItemStack(ClayiumItems.IMPURE_SODIUM_DUST.toStack(), 40),
                        new WeightedItemStack(ClayiumItems.IMPURE_CALCIUM_DUST.toStack(), 20),
                        new WeightedItemStack(ClayiumItems.IMPURE_POTASSIUM_DUST.toStack(), 15),
                        new WeightedItemStack(ClayiumItems.IMPURE_NICKEL_DUST.toStack(), 13),
                        new WeightedItemStack(ClayiumItems.IMPURE_ZINC_DUST.toStack(), 10),
                        new WeightedItemStack(ClayiumItems.IMPURE_IRON_DUST.toStack(), 9),
                        new WeightedItemStack(ClayiumItems.IMPURE_BERYLLIUM_DUST.toStack(), 8),
                        new WeightedItemStack(ClayiumItems.IMPURE_LITHIUM_DUST.toStack(), 7),
                        new WeightedItemStack(ClayiumItems.IMPURE_LEAD_DUST.toStack(), 6),
                        new WeightedItemStack(ClayiumItems.IMPURE_ZIRCONIUM_DUST.toStack(), 5),
                        new WeightedItemStack(ClayiumItems.IMPURE_HAFNIUM_DUST.toStack(), 4),
                        new WeightedItemStack(ClayiumItems.IMPURE_CHROME_DUST.toStack(), 3),
                        new WeightedItemStack(ClayiumItems.IMPURE_TITANIUM_DUST.toStack(), 3),
                        new WeightedItemStack(ClayiumItems.IMPURE_STRONTIUM_DUST.toStack(), 2),
                        new WeightedItemStack(ClayiumItems.IMPURE_BARIUM_DUST.toStack(), 2),
                        new WeightedItemStack(ClayiumItems.IMPURE_COPPER_DUST.toStack(), 1)),
                40, CEUtils.milliCeToLong(50), 6);
        recipeOutput.accept(Clayium.id("chemical_metal_separator/industrial_clay_dust"), recipe, null);
    }
}
