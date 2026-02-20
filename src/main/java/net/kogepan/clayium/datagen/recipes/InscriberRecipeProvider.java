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

public class InscriberRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack input1, ItemStack input2, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.INSCRIBER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(input1), ItemIngredientStack.of(input2)), List.of(result), duration,
                energy, tier);
        output.accept(Clayium.id("inscriber/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // CEE Board + 32 Energized Clay Dust -> CEE Circuit (Original: 2L, 20)
        create(recipeOutput, "cee_board_to_cee_circuit",
                ClayiumItems.CEE_BOARD.toStack(), ClayiumItems.ENERGIZED_CLAY_DUST.toStack(32),
                ClayiumItems.CEE_CIRCUIT.toStack(), CEUtils.TEN_MICRO_CE * 2, 20, 0);

        // Clay Circuit Board + 6 Dense Clay Dust -> Clay Circuit (Original: 2L, 20)
        create(recipeOutput, "clay_circuit_board_to_clay_circuit",
                ClayiumItems.CLAY_CIRCUIT_BOARD.toStack(), ClayiumItems.DENSE_CLAY_DUST.toStack(6),
                ClayiumItems.CLAY_CIRCUIT.toStack(), CEUtils.TEN_MICRO_CE * 2, 20, 0);

        // Clay Circuit Board + 32 Energized Clay Dust -> Basic Circuit (Original: 2L, 20)
        create(recipeOutput, "clay_circuit_board_to_basic_circuit",
                ClayiumItems.CLAY_CIRCUIT_BOARD.toStack(), ClayiumItems.ENERGIZED_CLAY_DUST.toStack(32),
                ClayiumItems.BASIC_CIRCUIT.toStack(), CEUtils.TEN_MICRO_CE * 2, 20, 0);

        // Impure Silicon Plate + 32 Energized Clay Dust -> Advanced Circuit (Original: 100L per tick, 120)
        create(recipeOutput, "impure_silicon_plate_to_advanced_circuit",
                ClayiumItems.IMPURE_SILICON_PLATE.toStack(), ClayiumItems.ENERGIZED_CLAY_DUST.toStack(32),
                ClayiumItems.ADVANCED_CIRCUIT.toStack(), CEUtils.ONE_MILLI_CE, 120, 0);

        // Silicon Plate + 32 Energized Clay Dust -> Precision Circuit (Original: 1000L per tick, 120)
        create(recipeOutput, "silicon_plate_to_precision_circuit",
                ClayiumItems.SILICON_PLATE.toStack(), ClayiumItems.ENERGIZED_CLAY_DUST.toStack(32),
                ClayiumItems.PRECISION_CIRCUIT.toStack(), CEUtils.ONE_MILLI_CE * 10, 120, 0);

        // spotless:on
    }
}
