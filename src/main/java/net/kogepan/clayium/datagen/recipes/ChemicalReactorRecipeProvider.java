package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChemicalReactorRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack input1, ItemStack input2, ItemStack result1,
                               ItemStack result2, long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.CHEMICAL_REACTOR_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(input1), ItemIngredientStack.of(input2)),
                List.of(result1, result2), duration, energy, tier);
        output.accept(Clayium.id("chemical_reactor/" + name), machineRecipe, null);
    }

    private static void create(RecipeOutput output, String name, ItemStack input1, ItemStack input2, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.CHEMICAL_REACTOR_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(input1), ItemIngredientStack.of(input2)), List.of(result), duration,
                energy, tier);
        output.accept(Clayium.id("chemical_reactor/" + name), machineRecipe, null);
    }

    private static void create(RecipeOutput output, String name, ItemStack input, List<ItemStack> results, long energy,
                               long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.CHEMICAL_REACTOR_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(input)), results, duration, energy, tier);
        output.accept(Clayium.id("chemical_reactor/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 0: (salt x2, calcareous clay dust) -> calcium chloride, sodium carbonate — e(5)=1000/tick, 120 (order-independent match)
        create(recipeOutput, "salt_calcareous_to_calcium_sodium",
                ClayiumItems.SALT_DUST.toStack(2), ClayiumItems.CALCAREOUS_CLAY_DUST.toStack(),
                ClayiumItems.CALCIUM_CHLORIDE_DUST.toStack(), ClayiumItems.SODIUM_CARBONATE_DUST.toStack(),
                CEUtils.ONE_MILLI_CE * 10, 120, 0);

        // Tier 0: (sodium carbonate, clay dust) -> quartz dust — e(4)=100/tick, 120
        create(recipeOutput, "sodium_carbonate_clay_to_quartz",
                ClayiumItems.SODIUM_CARBONATE_DUST.toStack(), ClayiumItems.CLAY_DUST.toStack(),
                ClayiumItems.QUARTZ_DUST.toStack(), CEUtils.ONE_MILLI_CE, 120, 0);

        // Tier 0: (quartz dust, coal) -> impure silicon ingot — e(4)=100/tick, 120
        create(recipeOutput, "quartz_coal_to_impure_silicon",
                ClayiumItems.QUARTZ_DUST.toStack(), new ItemStack(Items.COAL),
                ClayiumItems.IMPURE_SILICON_INGOT.toStack(), CEUtils.ONE_MILLI_CE, 120, 0);

        // Tier 0: (quartz dust, charcoal) -> impure silicon ingot — e(4)=100/tick, 120
        create(recipeOutput, "quartz_charcoal_to_impure_silicon",
                ClayiumItems.QUARTZ_DUST.toStack(), new ItemStack(Items.CHARCOAL),
                ClayiumItems.IMPURE_SILICON_INGOT.toStack(), CEUtils.ONE_MILLI_CE, 120, 0);

        // Tier 5: (dense clay dust) -> impure silicon dust, aluminium dust — e(5)=1000/tick, 30
        create(recipeOutput, "dense_clay_to_impure_silicon_aluminium",
                ClayiumItems.DENSE_CLAY_DUST.toStack(),
                List.of(ClayiumItems.IMPURE_SILICON_DUST.toStack(), ClayiumItems.ALUMINIUM_DUST.toStack()),
                CEUtils.ONE_MILLI_CE * 10, 30, 5);

        // Tier 5: (energized clay dust) -> impure redstone, impure glowstone dust — e(5)=1000/tick, 10
        create(recipeOutput, "energized_clay_to_impure_redstone_glowstone",
                ClayiumItems.ENERGIZED_CLAY_DUST.toStack(),
                List.of(ClayiumItems.IMPURE_REDSTONE_DUST.toStack(), ClayiumItems.IMPURE_GLOWSTONE_DUST.toStack()),
                CEUtils.ONE_MILLI_CE * 10, 10, 5);

        // Tier 8: (salt, industrial clay dust) -> quartz dust, calcium chloride — e(10,8)=100 CE/tick, 1
        create(recipeOutput, "salt_industrial_clay_to_quartz_calcium",
                ClayiumItems.SALT_DUST.toStack(), ClayiumItems.INDUSTRIAL_CLAY_DUST.toStack(),
                ClayiumItems.QUARTZ_DUST.toStack(), ClayiumItems.CALCIUM_CHLORIDE_DUST.toStack(),
                CEUtils.ceToLong(100), 1, 8);

        // Tier 8: (quartz dust, industrial clay dust) -> impure silicon ingot — e(10,8)=100 CE/tick, 1
        create(recipeOutput, "quartz_industrial_clay_to_impure_silicon",
                ClayiumItems.QUARTZ_DUST.toStack(), ClayiumItems.INDUSTRIAL_CLAY_DUST.toStack(),
                ClayiumItems.IMPURE_SILICON_INGOT.toStack(), CEUtils.ceToLong(100), 1, 8);

        // spotless:on
    }
}
