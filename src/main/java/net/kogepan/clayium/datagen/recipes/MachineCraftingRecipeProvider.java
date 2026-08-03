package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.NotNull;

public final class MachineCraftingRecipeProvider {

    private MachineCraftingRecipeProvider() {}

    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    public static void buildRecipes(@NotNull RecipeOutput output) {
        for (int tier = 1; tier <= 4; tier++) {
            buildBasicMachineRecipes(output, tier);
        }
        buildWaterwheelRecipes(output);
    }

    private static void buildBasicMachineRecipes(RecipeOutput output, int tier) {
        ItemLike hull = ClayiumBlocks.MACHINE_HULLS.get(tier).get();
        boolean clayParts = tier == 1;
        Item spindle = clayParts ? ClayiumItems.CLAY_SPINDLE.get() : ClayiumItems.DENSE_CLAY_SPINDLE.get();
        Item cylinder = clayParts ? ClayiumItems.CLAY_CYLINDER.get() : ClayiumItems.DENSE_CLAY_CYLINDER.get();
        Item gear = clayParts ? ClayiumItems.CLAY_GEAR.get() : ClayiumItems.DENSE_CLAY_GEAR.get();
        Item plate = clayParts ? ClayiumItems.CLAY_PLATE.get() : ClayiumItems.DENSE_CLAY_PLATE.get();
        Item pipe = clayParts ? ClayiumItems.CLAY_PIPE.get() : ClayiumItems.DENSE_CLAY_PIPE.get();
        Item cuttingHead = clayParts ? ClayiumItems.CLAY_CUTTING_HEAD.get()
                : ClayiumItems.DENSE_CLAY_CUTTING_HEAD.get();
        Item stick = clayParts ? ClayiumItems.CLAY_STICK.get() : ClayiumItems.DENSE_CLAY_STICK.get();

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(tier).get())
                .pattern("o-*")
                .pattern("P#P")
                .pattern("o-*")
                .define('#', hull)
                .define('o', spindle)
                .define('-', cylinder)
                .define('*', gear)
                .define('P', plate)
                .unlockedBy("has_machine_hull", has(hull))
                .save(output, Clayium.id("bending_machine_" + tier));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.WIRE_DRAWING_MACHINE_BLOCKS.get(tier).get())
                .pattern("*o*")
                .pattern("=#=")
                .pattern("*o*")
                .define('#', hull)
                .define('o', spindle)
                .define('*', gear)
                .define('=', pipe)
                .unlockedBy("has_machine_hull", has(hull))
                .save(output, Clayium.id("wire_drawing_machine_" + tier));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.PIPE_DRAWING_MACHINE_BLOCKS.get(tier).get())
                .pattern("*o*")
                .pattern("-#=")
                .pattern("*o*")
                .define('#', hull)
                .define('o', spindle)
                .define('-', cylinder)
                .define('*', gear)
                .define('=', pipe)
                .unlockedBy("has_machine_hull", has(hull))
                .save(output, Clayium.id("pipe_drawing_machine_" + tier));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.CUTTING_MACHINE_BLOCKS.get(tier).get())
                .pattern("P*P")
                .pattern("o#|")
                .pattern("P*P")
                .define('#', hull)
                .define('o', spindle)
                .define('*', gear)
                .define('P', plate)
                .define('|', cuttingHead)
                .unlockedBy("has_machine_hull", has(hull))
                .save(output, Clayium.id("cutting_machine_" + tier));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.LATHE_BLOCKS.get(tier).get())
                .pattern("P*P")
                .pattern("-#o")
                .pattern("P*P")
                .define('#', hull)
                .define('o', spindle)
                .define('-', stick)
                .define('*', gear)
                .define('P', plate)
                .unlockedBy("has_machine_hull", has(hull))
                .save(output, Clayium.id("lathe_" + tier));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.COBBLESTONE_GENERATORS.get(tier).get())
                .pattern(" * ")
                .pattern("=#=")
                .pattern(" * ")
                .define('#', hull)
                .define('*', gear)
                .define('=', pipe)
                .unlockedBy("has_machine_hull", has(hull))
                .save(output, Clayium.id("cobblestone_generator_" + tier));

        if (tier == 1 || tier >= 3) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.MILLING_MACHINE_BLOCKS.get(tier).get())
                    .pattern("P0P")
                    .pattern("o#o")
                    .pattern("P*P")
                    .define('#', hull)
                    .define('o', ClayiumItems.DENSE_CLAY_SPINDLE.get())
                    .define('*', ClayiumItems.DENSE_CLAY_GEAR.get())
                    .define('P', ClayiumItems.DENSE_CLAY_PLATE.get())
                    .define('0', ClayiumItems.DENSE_CLAY_CUTTING_HEAD.get())
                    .unlockedBy("has_machine_hull", has(hull))
                    .save(output, Clayium.id("milling_machine_" + tier));
        }
        if (tier >= 2) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.GRINDER_BLOCKS.get(tier).get())
                    .pattern("P0P")
                    .pattern("o#o")
                    .pattern("P*P")
                    .define('#', hull)
                    .define('o', spindle)
                    .define('*', gear)
                    .define('P', plate)
                    .define('0', ClayiumItems.DENSE_CLAY_GRINDING_HEAD.get())
                    .unlockedBy("has_machine_hull", has(hull))
                    .save(output, Clayium.id("grinder_" + tier));
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.DECOMPOSER_BLOCKS.get(tier).get())
                    .pattern("*o*")
                    .pattern("C#C")
                    .pattern("*=*")
                    .define('#', hull)
                    .define('o', spindle)
                    .define('*', gear)
                    .define('=', pipe)
                    .define('C', tier == 2 ? ClayiumItems.CLAY_CIRCUIT.get()
                            : tier == 3 ? ClayiumItems.SIMPLE_CIRCUIT.get() : ClayiumItems.BASIC_CIRCUIT.get())
                    .unlockedBy("has_machine_hull", has(hull))
                    .save(output, Clayium.id("decomposer_" + tier));
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.CLAY_CONDENSER_BLOCKS.get(tier).get())
                    .pattern("*P*")
                    .pattern("P#P")
                    .pattern("*P*")
                    .define('#', hull)
                    .define('*', gear)
                    .define('P', plate)
                    .unlockedBy("has_machine_hull", has(hull))
                    .save(output, Clayium.id("clay_condenser_" + tier));
        }
        if (tier >= 3) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.INSCRIBER_BLOCKS.get(tier).get())
                    .pattern("*o*")
                    .pattern("C#C")
                    .pattern("*C*")
                    .define('#', hull)
                    .define('o', spindle)
                    .define('*', gear)
                    .define('C', tier == 3 ? ClayiumItems.SIMPLE_CIRCUIT.get() : ClayiumItems.BASIC_CIRCUIT.get())
                    .unlockedBy("has_machine_hull", has(hull))
                    .save(output, Clayium.id("inscriber_" + tier));
        }
    }

    private static void buildWaterwheelRecipes(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ClayiumBlocks.WATERWHEELS.get(1).get())
                .requires(ClayiumBlocks.MACHINE_HULLS.get(1).get())
                .requires(ClayiumItems.CLAY_WHEEL.get())
                .unlockedBy("has_machine_hull", has(ClayiumBlocks.MACHINE_HULLS.get(1).get()))
                .save(output, Clayium.id("waterwheel_1"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ClayiumBlocks.WATERWHEELS.get(2).get())
                .requires(ClayiumBlocks.MACHINE_HULLS.get(2).get())
                .requires(ClayiumItems.DENSE_CLAY_WHEEL.get())
                .unlockedBy("has_machine_hull", has(ClayiumBlocks.MACHINE_HULLS.get(2).get()))
                .save(output, Clayium.id("waterwheel_2"));
    }
}
