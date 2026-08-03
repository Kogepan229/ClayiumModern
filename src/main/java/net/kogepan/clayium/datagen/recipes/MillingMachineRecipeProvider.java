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

public final class MillingMachineRecipeProvider {

    private MillingMachineRecipeProvider() {}

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result, long energy,
                               long duration, int tier) {
        MachineRecipe recipe = new MachineRecipe(ClayiumRecipeTypes.MILLING_MACHINE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);
        output.accept(Clayium.id("milling_machine/" + name), recipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput output) {
        // spotless:off

        // Tier 0
        create(output, "clay_circuit_board_from_dense_clay_plate", ClayiumItems.DENSE_CLAY_PLATE.toStack(), ClayiumItems.CLAY_CIRCUIT_BOARD.toStack(), CEUtils.TEN_MICRO_CE, 32, 0);
        create(output, "clay_circuit_board_from_industrial_clay_plate", ClayiumItems.INDUSTRIAL_CLAY_PLATE.toStack(), ClayiumItems.CLAY_CIRCUIT_BOARD.toStack(), CEUtils.TEN_MICRO_CE, 1, 0);

        // Tier 3
        create(output, "cee_board", ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_PLATE.toStack(), ClayiumItems.CEE_BOARD.toStack(), CEUtils.TEN_MICRO_CE * 2, 32, 3);

        // spotless:on
    }
}
