package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AssemblerRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack input, ItemStack result, long energy,
                               long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.ASSEMBLER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(input)), List.of(result), duration, energy, tier);
        output.accept(Clayium.id("assembler/" + name), machineRecipe, null);
    }

    private static void create(RecipeOutput output, String name, ItemStack input1, ItemStack input2, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.ASSEMBLER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(input1), ItemIngredientStack.of(input2)), List.of(result), duration,
                energy, tier);
        output.accept(Clayium.id("assembler/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 0
        create(recipeOutput, "quartz_crucible", ClayiumItems.QUARTZ_DUST.toStack(16), new ItemStack(ClayiumBlocks.QUARTZ_CRUCIBLE.get()), CEUtils.milliCeToLong(10), 20, 0);

        // Tier 3
        create(recipeOutput, "clay_gear_from_sticks", ClayiumItems.CLAY_STICK.toStack(5), ClayiumItems.CLAY_GEAR.toStack(), CEUtils.TEN_MICRO_CE * 10, 20, 3);
        create(recipeOutput, "clay_gear_from_short_sticks", ClayiumItems.SHORT_CLAY_STICK.toStack(9), ClayiumItems.CLAY_GEAR.toStack(), CEUtils.TEN_MICRO_CE * 10, 20, 3);
        create(recipeOutput, "clay_spindle", ClayiumItems.LARGE_CLAY_PLATE.toStack(), new ItemStack(Items.CLAY_BALL, 8), ClayiumItems.CLAY_SPINDLE.toStack(), CEUtils.TEN_MICRO_CE * 10, 20, 3);
        create(recipeOutput, "clay_grinding_head", ClayiumItems.LARGE_CLAY_PLATE.toStack(), new ItemStack(Blocks.CLAY, 8), ClayiumItems.CLAY_GRINDING_HEAD.toStack(), CEUtils.TEN_MICRO_CE * 10, 20, 3);
        create(recipeOutput, "clay_cutting_head", ClayiumItems.LARGE_CLAY_PLATE.toStack(), ClayiumItems.CLAY_PLATE.toStack(8), ClayiumItems.CLAY_CUTTING_HEAD.toStack(), CEUtils.TEN_MICRO_CE * 10, 20, 3);
        create(recipeOutput, "dense_clay_gear_from_sticks", ClayiumItems.DENSE_CLAY_STICK.toStack(5), ClayiumItems.DENSE_CLAY_GEAR.toStack(), CEUtils.TEN_MICRO_CE * 10, 20, 3);
        create(recipeOutput, "dense_clay_gear_from_short_sticks", ClayiumItems.DENSE_SHORT_CLAY_STICK.toStack(9), ClayiumItems.DENSE_CLAY_GEAR.toStack(), CEUtils.TEN_MICRO_CE * 10, 20, 3);
        create(recipeOutput, "dense_clay_spindle", ClayiumItems.DENSE_LARGE_CLAY_PLATE.toStack(), new ItemStack(Items.CLAY_BALL, 8), ClayiumItems.DENSE_CLAY_SPINDLE.toStack(), CEUtils.ONE_MILLI_CE, 20, 3);
        create(recipeOutput, "dense_clay_grinding_head", ClayiumItems.DENSE_LARGE_CLAY_PLATE.toStack(), new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(0).get(), 8), ClayiumItems.DENSE_CLAY_GRINDING_HEAD.toStack(), CEUtils.ONE_MILLI_CE, 20, 3);
        create(recipeOutput, "dense_clay_cutting_head", ClayiumItems.DENSE_LARGE_CLAY_PLATE.toStack(), ClayiumItems.DENSE_CLAY_PLATE.toStack(8), ClayiumItems.DENSE_CLAY_CUTTING_HEAD.toStack(), CEUtils.ONE_MILLI_CE, 20, 3);

        // Tier 4
        create(recipeOutput, "simple_item_filter", ClayiumItems.ADVANCED_CIRCUIT.toStack(), ClayiumItems.INDUSTRIAL_CLAY_PLATE.toStack(3), ClayiumItems.SIMPLE_ITEM_FILTER.toStack(), CEUtils.TEN_MICRO_CE * 8, 20, 4);
        create(recipeOutput, "fazy_item_filter", ClayiumItems.INTEGRATED_CIRCUIT.toStack(), ClayiumItems.INDUSTRIAL_CLAY_PLATE.toStack(3), ClayiumItems.FAZY_ITEM_FILTER.toStack(), CEUtils.TEN_MICRO_CE * 8, 20, 4);
        create(recipeOutput, "assembler_3_from_machine_hull", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(3).get()), ClayiumItems.DENSE_CLAY_GEAR.toStack(4), new ItemStack(ClayiumBlocks.ASSEMBLER_BLOCKS.get(3).get()), CEUtils.TEN_MICRO_CE * 10, 40, 4);
        create(recipeOutput, "assembler_4_from_machine_hull", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(4).get()), ClayiumItems.DENSE_CLAY_GEAR.toStack(4), new ItemStack(ClayiumBlocks.ASSEMBLER_BLOCKS.get(4).get()), CEUtils.ONE_MILLI_CE, 40, 4);
        create(recipeOutput, "assembler_6_from_machine_hull", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(6).get()), ClayiumItems.DENSE_CLAY_GEAR.toStack(4), new ItemStack(ClayiumBlocks.ASSEMBLER_BLOCKS.get(6).get()), CEUtils.milliCeToLong(100), 40, 4);
        create(recipeOutput, "assembler_6_from_basic_assembler", new ItemStack(ClayiumBlocks.ASSEMBLER_BLOCKS.get(4).get()), ClayiumItems.PRECISION_CIRCUIT.toStack(), new ItemStack(ClayiumBlocks.ASSEMBLER_BLOCKS.get(6).get()), CEUtils.milliCeToLong(100), 40, 4);

        // Tier 6
        create(recipeOutput, "synchronizer", ClayiumItems.AZ91D_ALLOY_PLATE.toStack(3), ClayiumItems.SYNCHRONOUS_PARTS.toStack(2), ClayiumItems.SYNCHRONIZER.toStack(), CEUtils.milliCeToLong(100), 20, 6);
        create(recipeOutput, "integrated_circuit", ClayiumItems.PRECISION_CIRCUIT.toStack(), ClayiumItems.ENERGIZED_CLAY_DUST.toStack(32), ClayiumItems.INTEGRATED_CIRCUIT.toStack(), CEUtils.milliCeToLong(100), 1200, 6);
        create(recipeOutput, "synchronous_parts", ClayiumItems.BERYLLIUM_DUST.toStack(8), ClayiumItems.INTEGRATED_CIRCUIT.toStack(), ClayiumItems.SYNCHRONOUS_PARTS.toStack(), CEUtils.milliCeToLong(100), 432000, 6);
        create(recipeOutput, "assembler_10", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(10).get()), new ItemStack(ClayiumBlocks.ASSEMBLER_BLOCKS.get(6).get()), new ItemStack(ClayiumBlocks.ASSEMBLER_BLOCKS.get(10).get()), CEUtils.ceToLong(1000), 40, 6);
        create(recipeOutput, "mossy_stone_bricks", new ItemStack(Blocks.STONE_BRICKS), new ItemStack(Blocks.VINE), new ItemStack(Blocks.MOSSY_STONE_BRICKS), CEUtils.milliCeToLong(100), 20, 6);

        // Tier 10
        create(recipeOutput, "saddle", new ItemStack(Items.LEATHER, 4), new ItemStack(Items.STRING, 16), new ItemStack(Items.SADDLE), CEUtils.ceToLong(1000), 6000, 10);
        create(recipeOutput, "name_tag", new ItemStack(Items.PAPER, 2), new ItemStack(Items.STRING, 4), new ItemStack(Items.NAME_TAG), CEUtils.ceToLong(1000), 600, 10);

        // spotless:on
    }
}
