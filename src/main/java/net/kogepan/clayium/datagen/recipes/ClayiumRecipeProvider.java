package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.recipes.AutoEatGadgetToggleRecipe;
import net.kogepan.clayium.recipes.recipes.StorageContainerUpgradeRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ClayiumRecipeProvider extends RecipeProvider {

    public ClayiumRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        OriginalCraftingRecipeProvider.buildRecipes(recipeOutput);
        MachineCraftingRecipeProvider.buildRecipes(recipeOutput);
        ClayWorkTableRecipeProvider.buildRecipes(recipeOutput);
        BendingMachineRecipeProvider.buildRecipes(recipeOutput);
        WireDrawingMachineRecipeProvider.buildRecipes(recipeOutput);
        PipeDrawingMachineRecipeProvider.buildRecipes(recipeOutput);
        CuttingMachineRecipeProvider.buildRecipes(recipeOutput);
        LatheRecipeProvider.buildRecipes(recipeOutput);
        MillingMachineRecipeProvider.buildRecipes(recipeOutput);
        ClayCondenserRecipeProvider.buildRecipes(recipeOutput);
        EnergeticClayCondenserRecipeProvider.buildRecipes(recipeOutput);
        GrinderRecipeProvider.buildRecipes(recipeOutput);
        CentrifugeRecipeProvider.buildRecipes(recipeOutput);
        ChemicalReactorRecipeProvider.buildRecipes(recipeOutput);
        ChemicalMetalSeparatorRecipeProvider.buildRecipes(recipeOutput);
        DecomposerRecipeProvider.buildRecipes(recipeOutput);
        ElectrolysisReactorRecipeProvider.buildRecipes(recipeOutput);
        InscriberRecipeProvider.buildRecipes(recipeOutput);
        AssemblerRecipeProvider.buildRecipes(recipeOutput);
        AssemblerMachineRecipeProvider.buildRecipes(recipeOutput);
        SmelterRecipeProvider.buildRecipes(recipeOutput);
        AlloySmelterRecipeProvider.buildRecipes(recipeOutput);
        ClayBlastFurnaceRecipeProvider.buildRecipes(recipeOutput);
        QuartzCrucibleRecipeProvider.buildRecipes(recipeOutput);
        LaserIrradiationRecipeProvider.buildRecipes(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ClayiumItems.CLAY_SHOVEL.get())
                .pattern("P")
                .pattern("S")
                .pattern("S")
                .define('P', ClayiumItems.CLAY_PLATE.get())
                .define('S', ClayiumItems.CLAY_STICK.get())
                .unlockedBy("has_clay_plate", has(ClayiumItems.CLAY_PLATE.get()))
                .save(recipeOutput, Clayium.id("clay_shovel"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ClayiumItems.CLAY_PICKAXE.get())
                .pattern("PPP")
                .pattern(" S ")
                .pattern(" S ")
                .define('P', ClayiumItems.DENSE_CLAY_PLATE.get())
                .define('S', ClayiumItems.DENSE_CLAY_STICK.get())
                .unlockedBy("has_dense_clay_plate", has(ClayiumItems.DENSE_CLAY_PLATE.get()))
                .save(recipeOutput, Clayium.id("clay_pickaxe"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ClayiumItems.CLAY_WRENCH.get())
                .pattern("B B")
                .pattern(" C ")
                .pattern(" S ")
                .define('B', ClayiumItems.DENSE_CLAY_BLADE.get())
                .define('C', ClayiumItems.DENSE_CLAY_SPINDLE.get())
                .define('S', ClayiumItems.DENSE_CLAY_STICK.get())
                .unlockedBy("has_dense_clay_blade", has(ClayiumItems.DENSE_CLAY_BLADE.get()))
                .save(recipeOutput, Clayium.id("clay_wrench"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.CLAY_CRAFTING_BOARD.get())
                .pattern("DDD")
                .define('D', ClayiumBlocks.COMPRESSED_CLAYS.get(0).get())
                .unlockedBy("has_dense_clay", has(ClayiumBlocks.COMPRESSED_CLAYS.get(0).get()))
                .save(recipeOutput, Clayium.id("clay_crafting_board"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS,
                ClayiumBlocks.COMPRESSED_CLAYS.get(0).get(), 3)
                .requires(ClayiumBlocks.CLAY_CRAFTING_BOARD.get())
                .unlockedBy("has_clay_crafting_board", has(ClayiumBlocks.CLAY_CRAFTING_BOARD.get()))
                .save(recipeOutput, Clayium.id("clay_crafting_board_to_dense_clay"));

        SpecialRecipeBuilder.special(StorageContainerUpgradeRecipe::new)
                .save(recipeOutput, Clayium.id("storage_container_upgrade"));
        SpecialRecipeBuilder.special(AutoEatGadgetToggleRecipe::new)
                .save(recipeOutput, Clayium.id("auto_eat_gadget_toggle"));

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

        // Assembler (tiers 3 and 4): *C* / o#o / *C*
        for (int tier : new int[] { 3, 4 }) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.ASSEMBLER_BLOCKS.get(tier).get())
                    .pattern("*C*")
                    .pattern("o#o")
                    .pattern("*C*")
                    .define('#', ClayiumBlocks.MACHINE_HULLS.get(tier).get())
                    .define('o', ClayiumItems.DENSE_CLAY_SPINDLE.get())
                    .define('*', ClayiumItems.DENSE_CLAY_GEAR.get())
                    .define('C', tier == 3 ? ClayiumItems.SIMPLE_CIRCUIT.get() : ClayiumItems.BASIC_CIRCUIT.get())
                    .unlockedBy("has_machine_hull", has(ClayiumBlocks.MACHINE_HULLS.get(tier).get()))
                    .save(recipeOutput, Clayium.id("assembler_" + tier));
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
