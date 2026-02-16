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
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GrinderRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result, long energy,
                               long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.GRINDER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);
        output.accept(Clayium.id("grinder/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Ore grinding (tier 0 for clay ore, tier 2 for dense)
        create(recipeOutput, "clay_ore", ClayiumBlocks.CLAY_ORE.get().asItem().getDefaultInstance(),
                ClayiumItems.CLAY_DUST.toStack(2), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "dense_clay_ore", ClayiumBlocks.DENSE_CLAY_ORE.get().asItem().getDefaultInstance(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(3), CEUtils.TEN_MICRO_CE, 6, 2);
        create(recipeOutput, "large_dense_clay_ore",
                ClayiumBlocks.LARGE_DENSE_CLAY_ORE.get().asItem().getDefaultInstance(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(5), CEUtils.TEN_MICRO_CE, 9, 2);

        // Clay block grinding
        create(recipeOutput, "clay_block", Blocks.CLAY.asItem().getDefaultInstance(),
                ClayiumItems.CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "compressed_clay_0_block",
                ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem().getDefaultInstance(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 8, 0);
        create(recipeOutput, "compressed_clay_2_block",
                ClayiumBlocks.COMPRESSED_CLAYS.get(2).asItem().getDefaultInstance(),
                ClayiumItems.INDUSTRIAL_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE, 12, 0);
        create(recipeOutput, "compressed_clay_3_block",
                ClayiumBlocks.COMPRESSED_CLAYS.get(3).asItem().getDefaultInstance(),
                ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE, 16, 0);

        // Clay part grinding (energy 1, time 3*i for CLAY i=1)
        create(recipeOutput, "clay_plate", ClayiumItems.CLAY_PLATE.toStack(), ClayiumItems.CLAY_DUST.toStack(),
                CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "clay_stick_4", ClayiumItems.CLAY_STICK.toStack(4), ClayiumItems.CLAY_DUST.toStack(),
                CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "short_clay_stick_8", ClayiumItems.SHORT_CLAY_STICK.toStack(8),
                ClayiumItems.CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "clay_ring_4", ClayiumItems.CLAY_RING.toStack(4), ClayiumItems.CLAY_DUST.toStack(5),
                CEUtils.TEN_MICRO_CE, 15, 0);
        create(recipeOutput, "small_clay_ring_8", ClayiumItems.SMALL_CLAY_RING.toStack(8),
                ClayiumItems.CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "clay_gear_8", ClayiumItems.CLAY_GEAR.toStack(8), ClayiumItems.CLAY_DUST.toStack(9),
                CEUtils.TEN_MICRO_CE, 27, 0);
        create(recipeOutput, "clay_blade", ClayiumItems.CLAY_BLADE.toStack(), ClayiumItems.CLAY_DUST.toStack(),
                CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "clay_needle", ClayiumItems.CLAY_NEEDLE.toStack(), ClayiumItems.CLAY_DUST.toStack(2),
                CEUtils.TEN_MICRO_CE, 6, 0);
        create(recipeOutput, "clay_disc_2", ClayiumItems.CLAY_DISC.toStack(2), ClayiumItems.CLAY_DUST.toStack(3),
                CEUtils.TEN_MICRO_CE, 9, 0);
        create(recipeOutput, "small_clay_disc_4", ClayiumItems.SMALL_CLAY_DISC.toStack(4),
                ClayiumItems.CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "clay_cylinder", ClayiumItems.CLAY_CYLINDER.toStack(), ClayiumItems.CLAY_DUST.toStack(2),
                CEUtils.TEN_MICRO_CE, 6, 0);
        create(recipeOutput, "clay_pipe", ClayiumItems.CLAY_PIPE.toStack(), ClayiumItems.CLAY_DUST.toStack(),
                CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "large_clay_plate", ClayiumItems.LARGE_CLAY_PLATE.toStack(),
                ClayiumItems.CLAY_DUST.toStack(4), CEUtils.TEN_MICRO_CE, 12, 0);
        create(recipeOutput, "clay_grinding_head", ClayiumItems.CLAY_GRINDING_HEAD.toStack(),
                ClayiumItems.CLAY_DUST.toStack(16), CEUtils.TEN_MICRO_CE, 48, 0);
        create(recipeOutput, "clay_bearing_4", ClayiumItems.CLAY_BEARING.toStack(4),
                ClayiumItems.CLAY_DUST.toStack(5), CEUtils.TEN_MICRO_CE, 15, 0);
        create(recipeOutput, "clay_spindle", ClayiumItems.CLAY_SPINDLE.toStack(), ClayiumItems.CLAY_DUST.toStack(4),
                CEUtils.TEN_MICRO_CE, 12, 0);
        create(recipeOutput, "clay_cutting_head", ClayiumItems.CLAY_CUTTING_HEAD.toStack(),
                ClayiumItems.CLAY_DUST.toStack(9), CEUtils.TEN_MICRO_CE, 27, 0);

        // Dense clay part grinding (time * 4)
        create(recipeOutput, "dense_clay_plate", ClayiumItems.DENSE_CLAY_PLATE.toStack(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 12, 0);
        create(recipeOutput, "dense_clay_stick_4", ClayiumItems.DENSE_CLAY_STICK.toStack(4),
                ClayiumItems.DENSE_CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 12, 0);
        create(recipeOutput, "dense_short_clay_stick_8", ClayiumItems.DENSE_SHORT_CLAY_STICK.toStack(8),
                ClayiumItems.DENSE_CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 12, 0);
        create(recipeOutput, "dense_clay_ring_4", ClayiumItems.DENSE_CLAY_RING.toStack(4),
                ClayiumItems.DENSE_CLAY_DUST.toStack(5), CEUtils.TEN_MICRO_CE, 60, 0);
        create(recipeOutput, "dense_small_clay_ring_8", ClayiumItems.DENSE_SMALL_CLAY_RING.toStack(8),
                ClayiumItems.DENSE_CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 12, 0);
        create(recipeOutput, "dense_clay_gear_8", ClayiumItems.DENSE_CLAY_GEAR.toStack(8),
                ClayiumItems.DENSE_CLAY_DUST.toStack(9), CEUtils.TEN_MICRO_CE, 108, 0);
        create(recipeOutput, "dense_clay_blade", ClayiumItems.DENSE_CLAY_BLADE.toStack(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 12, 0);
        create(recipeOutput, "dense_clay_needle", ClayiumItems.DENSE_CLAY_NEEDLE.toStack(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(2), CEUtils.TEN_MICRO_CE, 24, 0);
        create(recipeOutput, "dense_clay_disc_2", ClayiumItems.DENSE_CLAY_DISC.toStack(2),
                ClayiumItems.DENSE_CLAY_DUST.toStack(3), CEUtils.TEN_MICRO_CE, 36, 0);
        create(recipeOutput, "dense_small_clay_disc_4", ClayiumItems.DENSE_SMALL_CLAY_DISC.toStack(4),
                ClayiumItems.DENSE_CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 12, 0);
        create(recipeOutput, "dense_clay_cylinder", ClayiumItems.DENSE_CLAY_CYLINDER.toStack(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(2), CEUtils.TEN_MICRO_CE, 24, 0);
        create(recipeOutput, "dense_clay_pipe", ClayiumItems.DENSE_CLAY_PIPE.toStack(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(), CEUtils.TEN_MICRO_CE, 12, 0);
        create(recipeOutput, "dense_large_clay_plate", ClayiumItems.DENSE_LARGE_CLAY_PLATE.toStack(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(4), CEUtils.TEN_MICRO_CE, 48, 0);
        create(recipeOutput, "dense_clay_grinding_head", ClayiumItems.DENSE_CLAY_GRINDING_HEAD.toStack(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(16), CEUtils.TEN_MICRO_CE, 192, 0);
        create(recipeOutput, "dense_clay_bearing_4", ClayiumItems.DENSE_CLAY_BEARING.toStack(4),
                ClayiumItems.DENSE_CLAY_DUST.toStack(5), CEUtils.TEN_MICRO_CE, 60, 0);
        create(recipeOutput, "dense_clay_spindle", ClayiumItems.DENSE_CLAY_SPINDLE.toStack(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(4), CEUtils.TEN_MICRO_CE, 48, 0);
        create(recipeOutput, "dense_clay_cutting_head", ClayiumItems.DENSE_CLAY_CUTTING_HEAD.toStack(),
                ClayiumItems.DENSE_CLAY_DUST.toStack(9), CEUtils.TEN_MICRO_CE, 108, 0);

        // Vanilla compatibility
        create(recipeOutput, "cobblestone_to_gravel", Blocks.COBBLESTONE.asItem().getDefaultInstance(),
                Blocks.GRAVEL.asItem().getDefaultInstance(), CEUtils.TEN_MICRO_CE, 10, 0);
        create(recipeOutput, "gravel_to_sand", Blocks.GRAVEL.asItem().getDefaultInstance(),
                Blocks.SAND.asItem().getDefaultInstance(), CEUtils.TEN_MICRO_CE, 10, 2);

        // spotless:on
    }
}
