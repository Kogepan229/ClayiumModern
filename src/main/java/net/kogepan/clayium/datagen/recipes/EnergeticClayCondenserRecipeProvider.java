package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class EnergeticClayCondenserRecipeProvider {

    private EnergeticClayCondenserRecipeProvider() {}

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result, long energy,
                               long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(
                ClayiumRecipeTypes.ENERGETIC_CLAY_CONDENSER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);
        output.accept(Clayium.id("energetic_clay_condenser/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 3
        create(recipeOutput, "compressed_3_to_4", new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(3).asItem(), 9), ClayiumBlocks.COMPRESSED_CLAYS.get(4).asItem().getDefaultInstance(), CEUtils.TEN_MICRO_CE, 16, 3);
        create(recipeOutput, "compressed_4_to_5", new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(4).asItem(), 9), ClayiumBlocks.COMPRESSED_CLAYS.get(5).asItem().getDefaultInstance(), CEUtils.TEN_MICRO_CE * 10, 32, 3);
        create(recipeOutput, "compressed_5_to_6", new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(5).asItem(), 9), ClayiumBlocks.COMPRESSED_CLAYS.get(6).asItem().getDefaultInstance(), CEUtils.ONE_MILLI_CE, 64, 3);

        // spotless:on
    }
}
