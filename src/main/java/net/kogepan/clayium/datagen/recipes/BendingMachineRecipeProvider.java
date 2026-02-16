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

public class BendingMachineRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.BENDING_MACHINE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);

        output.accept(Clayium.id("bending_machine/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 0
        create(recipeOutput, "clay_place", Blocks.CLAY.asItem().getDefaultInstance(), ClayiumItems.CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE, 1, 0);
        create(recipeOutput, "raw_clay_slicer", ClayiumItems.CLAY_DISC.toStack(), ClayiumItems.RAW_CLAY_SLICER.toStack(), CEUtils.TEN_MICRO_CE, 1, 0);
        create(recipeOutput, "clay_disc", ClayiumItems.LARGE_CLAY_BALL.toStack(), ClayiumItems.CLAY_DISC.toStack(), CEUtils.TEN_MICRO_CE, 2, 0);
        create(recipeOutput, "small_clay_disc", Items.CLAY.getDefaultInstance(), ClayiumItems.SMALL_CLAY_DISC.toStack(), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "dense_clay_plate", ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem().getDefaultInstance(), ClayiumItems.DENSE_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "clay_blade", ClayiumItems.CLAY_CYLINDER.toStack(), ClayiumItems.CLAY_BLADE.toStack(2), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "large_clay_place", ClayiumItems.CLAY_PLATE.toStack(4), ClayiumItems.LARGE_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "dense_clay_blade", ClayiumItems.DENSE_CLAY_CYLINDER.toStack(), ClayiumItems.DENSE_CLAY_BLADE.toStack(2), CEUtils.TEN_MICRO_CE, 8, 0);
        create(recipeOutput, "dense_large_clay_place", ClayiumItems.DENSE_CLAY_PLATE.toStack(4), ClayiumItems.DENSE_LARGE_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE, 8, 0);

        // Tier 2
        create(recipeOutput, "industrial_clay_plate", ClayiumBlocks.COMPRESSED_CLAYS.get(2).asItem().getDefaultInstance(), ClayiumItems.INDUSTRIAL_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE * 2, 4, 2);
        create(recipeOutput, "industrial_clay_plate_from_dust", ClayiumItems.INDUSTRIAL_CLAY_DUST.toStack(4), ClayiumItems.INDUSTRIAL_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE * 2, 4, 2);
        create(recipeOutput, "large_industrial_clay_plate", ClayiumItems.INDUSTRIAL_CLAY_PLATE.toStack(4), ClayiumItems.LARGE_INDUSTRIAL_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE * 2, 8, 2);
        create(recipeOutput, "advanced_industrial_clay_plate", ClayiumBlocks.COMPRESSED_CLAYS.get(3).asItem().getDefaultInstance(), ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE * 4, 4, 2);
        create(recipeOutput, "advanced_industrial_clay_plate_from_dust", ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(4), ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE * 4, 4, 2);
        create(recipeOutput, "large_advanced_industrial_clay_plate", ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_PLATE.toStack(4), ClayiumItems.LARGE_ADVANCED_INDUSTRIAL_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE * 4, 8, 2);

        // Tier 4
        create(recipeOutput, "impure_silicon_plate", ClayiumItems.IMPURE_SILICON_INGOT.toStack(), ClayiumItems.IMPURE_SILICON_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 20, 4);
        create(recipeOutput, "large_impure_silicon_plate", ClayiumItems.IMPURE_SILICON_PLATE.toStack(4), ClayiumItems.LARGE_IMPURE_SILICON_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 40, 4);
        create(recipeOutput, "silicon_plate", ClayiumItems.SILICON_INGOT.toStack(), ClayiumItems.SILICON_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 20, 4);
        create(recipeOutput, "large_silicon_plate", ClayiumItems.SILICON_PLATE.toStack(4), ClayiumItems.LARGE_SILICON_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 40, 4);
        create(recipeOutput, "silicone_plate", ClayiumItems.SILICONE_INGOT.toStack(), ClayiumItems.SILICONE_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 4, 4);
        create(recipeOutput, "large_silicone_plate", ClayiumItems.SILICONE_PLATE.toStack(4), ClayiumItems.LARGE_SILICONE_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 8, 4);
        create(recipeOutput, "aluminium_plate", ClayiumItems.ALUMINIUM_INGOT.toStack(), ClayiumItems.ALUMINIUM_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 20, 4);
        create(recipeOutput, "large_aluminium_plate", ClayiumItems.ALUMINIUM_PLATE.toStack(4), ClayiumItems.LARGE_ALUMINIUM_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 40, 4);
        create(recipeOutput, "impure_aluminium_plate", ClayiumItems.IMPURE_ALUMINIUM_INGOT.toStack(), ClayiumItems.IMPURE_ALUMINIUM_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 20, 4);
        create(recipeOutput, "large_impure_aluminium_plate", ClayiumItems.IMPURE_ALUMINIUM_PLATE.toStack(4), ClayiumItems.LARGE_IMPURE_ALUMINIUM_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 40, 4);
        create(recipeOutput, "clay_steel_plate", ClayiumItems.CLAY_STEEL_INGOT.toStack(), ClayiumItems.CLAY_STEEL_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 60, 4);
        create(recipeOutput, "large_clay_steel_plate", ClayiumItems.CLAY_STEEL_PLATE.toStack(4), ClayiumItems.LARGE_CLAY_STEEL_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 120, 4);
        create(recipeOutput, "clayium_plate", ClayiumItems.CLAYIUM_INGOT.toStack(), ClayiumItems.CLAYIUM_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 120, 4);
        create(recipeOutput, "large_clayium_plate", ClayiumItems.CLAYIUM_PLATE.toStack(4), ClayiumItems.LARGE_CLAYIUM_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 240, 4);
        create(recipeOutput, "ultimate_alloy_plate", ClayiumItems.ULTIMATE_ALLOY_INGOT.toStack(), ClayiumItems.ULTIMATE_ALLOY_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 180, 4);
        create(recipeOutput, "large_ultimate_alloy_plate", ClayiumItems.ULTIMATE_ALLOY_PLATE.toStack(4), ClayiumItems.LARGE_ULTIMATE_ALLOY_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 360, 4);
        create(recipeOutput, "az91d_alloy_plate", ClayiumItems.AZ91D_ALLOY_INGOT.toStack(), ClayiumItems.AZ91D_ALLOY_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 20, 4);
        create(recipeOutput, "large_az91d_alloy_plate", ClayiumItems.AZ91D_ALLOY_PLATE.toStack(4), ClayiumItems.LARGE_AZ91D_ALLOY_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 40, 4);
        create(recipeOutput, "zk60a_alloy_plate", ClayiumItems.ZK60A_ALLOY_INGOT.toStack(), ClayiumItems.ZK60A_ALLOY_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 20, 4);
        create(recipeOutput, "large_zk60a_alloy_plate", ClayiumItems.ZK60A_ALLOY_PLATE.toStack(4), ClayiumItems.LARGE_ZK60A_ALLOY_PLATE.toStack(), CEUtils.ONE_MILLI_CE, 40, 4);

        // Tier 9
        create(recipeOutput, "antimatter_plate", ClayiumItems.ANTIMATTER_MATTER.toStack(), ClayiumItems.ANTIMATTER_PLATE.toStack(), CEUtils.ceToLong(100), 20, 9);
        create(recipeOutput, "large_antimatter_plate", ClayiumItems.ANTIMATTER_PLATE.toStack(4), ClayiumItems.LARGE_ANTIMATTER_PLATE.toStack(), CEUtils.ceToLong(100), 40, 9);
        create(recipeOutput, "pure_antimatter_plate", ClayiumItems.PURE_ANTIMATTER_MATTER.toStack(), ClayiumItems.PURE_ANTIMATTER_PLATE.toStack(), CEUtils.ceToLong(1000), 20, 9);
        create(recipeOutput, "large_pure_antimatter_plate", ClayiumItems.PURE_ANTIMATTER_PLATE.toStack(4), ClayiumItems.LARGE_PURE_ANTIMATTER_PLATE.toStack(), CEUtils.ceToLong(1000), 40, 9);
        create(recipeOutput, "octuple_energetic_clay_plate", ClayiumBlocks.COMPRESSED_CLAYS.get(12).asItem().getDefaultInstance(), ClayiumItems.OCTUPLE_ENERGETIC_CLAY_PLATE.toStack(), CEUtils.ceToLong(10000), 20, 9);
        create(recipeOutput, "large_octuple_energetic_clay_plate", ClayiumItems.OCTUPLE_ENERGETIC_CLAY_PLATE.toStack(4), ClayiumItems.LARGE_OCTUPLE_ENERGETIC_CLAY_PLATE.toStack(), CEUtils.ceToLong(10000), 40, 9);
        create(recipeOutput, "octuple_pure_antimatter_plate", ClayiumItems.COMPRESSED_PURE_ANTIMATTER_8.toStack(), ClayiumItems.OCTUPLE_PURE_ANTIMATTER_PLATE.toStack(), CEUtils.ceToLong(100000), 20, 9);
        create(recipeOutput, "large_octuple_pure_antimatter_plate", ClayiumItems.OCTUPLE_PURE_ANTIMATTER_PLATE.toStack(4), ClayiumItems.LARGE_OCTUPLE_PURE_ANTIMATTER_PLATE.toStack(), CEUtils.ceToLong(100000), 40, 9);

        // spotless:on
    }
}
