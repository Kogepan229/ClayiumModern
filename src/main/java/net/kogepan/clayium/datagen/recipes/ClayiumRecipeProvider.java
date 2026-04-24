package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ClayiumRecipeProvider extends RecipeProvider {

    public ClayiumRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ClayWorkTableRecipeProvider.buildRecipes(recipeOutput);
        BendingMachineRecipeProvider.buildRecipes(recipeOutput);
        WireDrawingMachineRecipeProvider.buildRecipes(recipeOutput);
        PipeDrawingMachineRecipeProvider.buildRecipes(recipeOutput);
        CuttingMachineRecipeProvider.buildRecipes(recipeOutput);
        LatheRecipeProvider.buildRecipes(recipeOutput);
        MillingMachineRecipeProvider.buildRecipes(recipeOutput);
        ClayCondenserRecipeProvider.buildRecipes(recipeOutput);
        GrinderRecipeProvider.buildRecipes(recipeOutput);
        CentrifugeRecipeProvider.buildRecipes(recipeOutput);
        ChemicalReactorRecipeProvider.buildRecipes(recipeOutput);
        DecomposerRecipeProvider.buildRecipes(recipeOutput);
        ElectrolysisReactorRecipeProvider.buildRecipes(recipeOutput);
        InscriberRecipeProvider.buildRecipes(recipeOutput);
        SmelterRecipeProvider.buildRecipes(recipeOutput);
        ClayBlastFurnaceRecipeProvider.buildRecipes(recipeOutput);
        QuartzCrucibleRecipeProvider.buildRecipes(recipeOutput);
        LaserIrradiationRecipeProvider.buildRecipes(recipeOutput);

        // Laser Reflector
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.LASER_REFLECTOR.get())
                .pattern(" G ")
                .pattern("GLG")
                .pattern(" G ")
                .define('G', Items.GLASS_PANE)
                .define('L', ClayiumBlocks.CLAY_LASER_BLOCKS.get(7).get())
                .unlockedBy("has_clay_laser", has(ClayiumBlocks.CLAY_LASER_BLOCKS.get(7).get()))
                .save(recipeOutput, Clayium.id("laser_reflector"));

        // Quartz Crucible
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.QUARTZ_CRUCIBLE.get())
                .pattern("Q Q")
                .pattern("Q Q")
                .pattern("QQQ")
                .define('Q', Items.QUARTZ)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(recipeOutput, Clayium.id("quartz_crucible"));

        // Centrifuge (tiers 3, 4, 5, 6): *o* / o#o / *o* — # = machine hull, o = spindle, * = gear
        for (int tier : new int[] { 3, 4, 5, 6 }) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.CENTRIFUGE_BLOCKS.get(tier).get())
                    .pattern("*o*")
                    .pattern("o#o")
                    .pattern("*o*")
                    .define('#', ClayiumBlocks.MACHINE_HULLS.get(tier).get())
                    .define('o', ClayiumItems.DENSE_CLAY_SPINDLE.get())
                    .define('*', ClayiumItems.DENSE_CLAY_GEAR.get())
                    .unlockedBy("has_machine_hull", has(ClayiumBlocks.MACHINE_HULLS.get(tier).get()))
                    .save(recipeOutput, Clayium.id("centrifuge_" + tier));
        }

        // Chemical Reactor (tiers 4, 5, 8): *o* / o#o / *o* — # = machine hull, o = spindle, * = gear
        for (int tier : new int[] { 4, 5, 8 }) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS.get(tier).get())
                    .pattern("*o*")
                    .pattern("o#o")
                    .pattern("*o*")
                    .define('#', ClayiumBlocks.MACHINE_HULLS.get(tier).get())
                    .define('o', ClayiumItems.DENSE_CLAY_SPINDLE.get())
                    .define('*', ClayiumItems.DENSE_CLAY_GEAR.get())
                    .unlockedBy("has_machine_hull", has(ClayiumBlocks.MACHINE_HULLS.get(tier).get()))
                    .save(recipeOutput, Clayium.id("chemical_reactor_" + tier));
        }

        // Salt Extractor (tiers 4, 5, 6, 7): Buffer + Basic Circuit
        for (int tier : new int[] { 4, 5, 6, 7 }) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.SALT_EXTRACTOR_BLOCKS.get(tier).get())
                    .pattern("BC")
                    .define('B', ClayiumBlocks.CLAY_BUFFERS.get(tier).get())
                    .define('C', ClayiumItems.BASIC_CIRCUIT.get())
                    .unlockedBy("has_basic_circuit", has(ClayiumItems.BASIC_CIRCUIT.get()))
                    .save(recipeOutput, Clayium.id("salt_extractor_" + tier));
        }

        // Electrolysis Reactor (tiers 6, 7, 8, 9): Chemical Reactor + circuit progression
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.ELECTROLYSIS_REACTOR_BLOCKS.get(6).get())
                .pattern("RC")
                .define('R', ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS.get(5).get())
                .define('C', ClayiumItems.PRECISION_CIRCUIT.get())
                .unlockedBy("has_precision_circuit", has(ClayiumItems.PRECISION_CIRCUIT.get()))
                .save(recipeOutput, Clayium.id("electrolysis_reactor_6"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.ELECTROLYSIS_REACTOR_BLOCKS.get(7).get())
                .pattern("RC")
                .define('R', ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS.get(5).get())
                .define('C', ClayiumItems.INTEGRATED_CIRCUIT.get())
                .unlockedBy("has_integrated_circuit", has(ClayiumItems.INTEGRATED_CIRCUIT.get()))
                .save(recipeOutput, Clayium.id("electrolysis_reactor_7"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.ELECTROLYSIS_REACTOR_BLOCKS.get(8).get())
                .pattern("RC")
                .define('R', ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS.get(5).get())
                .define('C', ClayiumItems.CLAY_CORE.get())
                .unlockedBy("has_clay_core", has(ClayiumItems.CLAY_CORE.get()))
                .save(recipeOutput, Clayium.id("electrolysis_reactor_8"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.ELECTROLYSIS_REACTOR_BLOCKS.get(9).get())
                .pattern("RC")
                .define('R', ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS.get(5).get())
                .define('C', ClayiumItems.CLAY_BRAIN.get())
                .unlockedBy("has_clay_brain", has(ClayiumItems.CLAY_BRAIN.get()))
                .save(recipeOutput, Clayium.id("electrolysis_reactor_9"));

        // Distributor (tiers 7, 8, 9): Buffer + Machine Hull
        for (int tier : new int[] { 7, 8, 9 }) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.DISTRIBUTOR_BLOCKS.get(tier).get())
                    .pattern("BH")
                    .define('B', ClayiumBlocks.CLAY_BUFFERS.get(tier).get())
                    .define('H', ClayiumBlocks.MACHINE_HULLS.get(tier).get())
                    .unlockedBy("has_clay_buffer", has(ClayiumBlocks.CLAY_BUFFERS.get(tier).get()))
                    .save(recipeOutput, Clayium.id("distributor_" + tier));
        }
    }
}
