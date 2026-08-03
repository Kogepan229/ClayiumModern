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
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

public final class OriginalCraftingRecipeProvider {

    private OriginalCraftingRecipeProvider() {}

    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    public static void buildRecipes(@NotNull RecipeOutput output) {
        buildCompressionRecipes(output);
        buildClayMaterialRecipes(output);
        buildToolRecipes(output);
        buildMaterialSmeltingRecipes(output);
        buildHullAndCircuitRecipes(output);
        buildUtilityRecipes(output);
    }

    private static void buildCompressionRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ClayiumBlocks.COMPRESSED_CLAYS.get(0).get())
                .pattern("CCC")
                .pattern("CCC")
                .pattern("CCC")
                .define('C', Blocks.CLAY)
                .unlockedBy("has_clay", has(Blocks.CLAY))
                .save(output, Clayium.id("dense_clay"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.CLAY, 9)
                .requires(ClayiumBlocks.COMPRESSED_CLAYS.get(0).get())
                .unlockedBy("has_dense_clay", has(ClayiumBlocks.COMPRESSED_CLAYS.get(0).get()))
                .save(output, Clayium.id("dense_clay_to_clay"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ClayiumBlocks.COMPRESSED_CLAYS.get(1).get())
                .pattern("DDD")
                .pattern("DDD")
                .pattern("DDD")
                .define('D', ClayiumBlocks.COMPRESSED_CLAYS.get(0).get())
                .unlockedBy("has_dense_clay", has(ClayiumBlocks.COMPRESSED_CLAYS.get(0).get()))
                .save(output, Clayium.id("compressed_clay"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS,
                ClayiumBlocks.COMPRESSED_CLAYS.get(0).get(), 9)
                .requires(ClayiumBlocks.COMPRESSED_CLAYS.get(1).get())
                .unlockedBy("has_compressed_clay", has(ClayiumBlocks.COMPRESSED_CLAYS.get(1).get()))
                .save(output, Clayium.id("compressed_clay_to_dense_clay"));
    }

    private static void buildClayMaterialRecipes(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ClayiumItems.LARGE_CLAY_BALL.get())
                .requires(net.minecraft.world.item.Items.CLAY_BALL, 8)
                .unlockedBy("has_clay_ball", has(net.minecraft.world.item.Items.CLAY_BALL))
                .save(output, Clayium.id("large_clay_ball_from_clay_balls"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ClayiumItems.SHORT_CLAY_STICK.get(), 2)
                .requires(ClayiumItems.CLAY_STICK.get())
                .unlockedBy("has_clay_stick", has(ClayiumItems.CLAY_STICK.get()))
                .save(output, Clayium.id("short_clay_stick_from_clay_stick"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ClayiumItems.SMALL_CLAY_RING.get())
                .requires(ClayiumItems.SHORT_CLAY_STICK.get())
                .unlockedBy("has_short_clay_stick", has(ClayiumItems.SHORT_CLAY_STICK.get()))
                .save(output, Clayium.id("small_clay_ring_from_short_clay_stick"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ClayiumItems.SHORT_CLAY_STICK.get())
                .requires(ClayiumItems.SMALL_CLAY_RING.get())
                .unlockedBy("has_small_clay_ring", has(ClayiumItems.SMALL_CLAY_RING.get()))
                .save(output, Clayium.id("short_clay_stick_from_small_clay_ring"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ClayiumItems.CLAY_RING.get())
                .requires(ClayiumItems.CLAY_CYLINDER.get())
                .unlockedBy("has_clay_cylinder", has(ClayiumItems.CLAY_CYLINDER.get()))
                .save(output, Clayium.id("clay_ring_from_clay_cylinder"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ClayiumItems.CLAY_PIPE.get())
                .requires(ClayiumItems.CLAY_PLATE.get())
                .unlockedBy("has_clay_plate", has(ClayiumItems.CLAY_PLATE.get()))
                .save(output, Clayium.id("clay_pipe_from_clay_plate"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumItems.LARGE_CLAY_PLATE.get())
                .pattern("PPP")
                .pattern("PPP")
                .pattern("PPP")
                .define('P', ClayiumItems.CLAY_PLATE.get())
                .unlockedBy("has_clay_plate", has(ClayiumItems.CLAY_PLATE.get()))
                .save(output, Clayium.id("large_clay_plate_from_clay_plates"));

        buildComponentRecipes(output, "clay", ClayiumItems.CLAY_PLATE.get(), ClayiumItems.CLAY_STICK.get(),
                ClayiumItems.SHORT_CLAY_STICK.get(), ClayiumItems.CLAY_RING.get(),
                ClayiumItems.SMALL_CLAY_RING.get(), ClayiumItems.CLAY_GEAR.get(), ClayiumItems.CLAY_BLADE.get(),
                ClayiumItems.CLAY_NEEDLE.get(), net.minecraft.world.item.Items.CLAY_BALL,
                ClayiumItems.CLAY_BEARING.get(), ClayiumItems.CLAY_SPINDLE.get(),
                ClayiumItems.CLAY_CUTTING_HEAD.get(), ClayiumItems.CLAY_GRINDING_HEAD.get(),
                ClayiumItems.CLAY_WHEEL.get(), true);
        buildComponentRecipes(output, "dense_clay", ClayiumItems.DENSE_CLAY_PLATE.get(),
                ClayiumItems.DENSE_CLAY_STICK.get(), ClayiumItems.DENSE_SHORT_CLAY_STICK.get(),
                ClayiumItems.DENSE_CLAY_RING.get(), ClayiumItems.DENSE_SMALL_CLAY_RING.get(),
                ClayiumItems.DENSE_CLAY_GEAR.get(), ClayiumItems.DENSE_CLAY_BLADE.get(),
                ClayiumItems.DENSE_CLAY_NEEDLE.get(), null, ClayiumItems.DENSE_CLAY_BEARING.get(),
                ClayiumItems.DENSE_CLAY_SPINDLE.get(), ClayiumItems.DENSE_CLAY_CUTTING_HEAD.get(),
                ClayiumItems.DENSE_CLAY_GRINDING_HEAD.get(), ClayiumItems.DENSE_CLAY_WHEEL.get(), false);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, net.minecraft.world.item.Items.CLAY_BALL, 3)
                .requires(ClayiumItems.CLAY_RING.get())
                .unlockedBy("has_clay_ring", has(ClayiumItems.CLAY_RING.get()))
                .save(output, Clayium.id("clay_ring_to_clay_balls"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, net.minecraft.world.item.Items.CLAY_BALL, 3)
                .requires(ClayiumItems.CLAY_GEAR.get())
                .unlockedBy("has_clay_gear", has(ClayiumItems.CLAY_GEAR.get()))
                .save(output, Clayium.id("clay_gear_to_clay_balls"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, net.minecraft.world.item.Items.CLAY_BALL, 3)
                .requires(ClayiumItems.CLAY_BLADE.get())
                .unlockedBy("has_clay_blade", has(ClayiumItems.CLAY_BLADE.get()))
                .save(output, Clayium.id("clay_blade_to_clay_balls"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, net.minecraft.world.item.Items.CLAY_BALL, 5)
                .requires(ClayiumItems.CLAY_NEEDLE.get())
                .unlockedBy("has_clay_needle", has(ClayiumItems.CLAY_NEEDLE.get()))
                .save(output, Clayium.id("clay_needle_to_clay_balls"));
    }

    private static void buildComponentRecipes(RecipeOutput output, String prefix, ItemLike plate, ItemLike stick,
                                               ItemLike shortStick, ItemLike ring, ItemLike smallRing, ItemLike gear,
                                               ItemLike blade, ItemLike needle, ItemLike ball, ItemLike bearing,
                                               ItemLike spindle, ItemLike cuttingHead, ItemLike grindingHead,
                                               ItemLike wheel, boolean buildBearing) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, gear)
                .pattern("iii")
                .pattern("ioi")
                .pattern("iii")
                .define('i', shortStick)
                .define('o', smallRing)
                .unlockedBy("has_short_stick", has(shortStick))
                .save(output, Clayium.id(prefix + "_gear_crafting"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, cuttingHead)
                .pattern("iii")
                .pattern("ioi")
                .pattern("iii")
                .define('i', blade)
                .define('o', ring)
                .unlockedBy("has_blade", has(blade))
                .save(output, Clayium.id(prefix + "_cutting_head_crafting"));
        if (buildBearing) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, bearing)
                    .pattern("iii")
                    .pattern("ioi")
                    .pattern("iii")
                    .define('i', ball)
                    .define('o', ring)
                    .unlockedBy("has_ball", has(ball))
                    .save(output, Clayium.id(prefix + "_bearing_crafting"));
        }
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, spindle)
                .pattern("0#0")
                .pattern("ioO")
                .pattern("0#0")
                .define('i', stick)
                .define('o', bearing)
                .define('O', ring)
                .define('#', plate)
                .define('0', smallRing)
                .unlockedBy("has_bearing", has(bearing))
                .save(output, Clayium.id(prefix + "_spindle_crafting"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, grindingHead)
                .pattern("iii")
                .pattern("ioi")
                .pattern("iii")
                .define('i', needle)
                .define('o', ring)
                .unlockedBy("has_needle", has(needle))
                .save(output, Clayium.id(prefix + "_grinding_head_crafting"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, wheel)
                .pattern("###")
                .pattern("#o#")
                .pattern("###")
                .define('#', plate)
                .define('o', ring)
                .unlockedBy("has_plate", has(plate))
                .save(output, Clayium.id(prefix + "_wheel_crafting"));
    }

    private static void buildToolRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ClayiumItems.RAW_CLAY_ROLLING_PIN.get())
                .pattern("-=-")
                .define('-', ClayiumItems.SHORT_CLAY_STICK.get())
                .define('=', ClayiumItems.CLAY_CYLINDER.get())
                .unlockedBy("has_clay_cylinder", has(ClayiumItems.CLAY_CYLINDER.get()))
                .save(output, Clayium.id("raw_clay_rolling_pin"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ClayiumItems.RAW_CLAY_SPATULA.get())
                .pattern("-=")
                .define('-', ClayiumItems.SHORT_CLAY_STICK.get())
                .define('=', ClayiumItems.CLAY_BLADE.get())
                .unlockedBy("has_clay_blade", has(ClayiumItems.CLAY_BLADE.get()))
                .save(output, Clayium.id("raw_clay_spatula"));
        addSmelting(output, "clay_rolling_pin", ClayiumItems.RAW_CLAY_ROLLING_PIN.get(),
                ClayiumItems.CLAY_ROLLING_PIN.get(), 0.1F, RecipeCategory.TOOLS);
        addSmelting(output, "clay_slicer", ClayiumItems.RAW_CLAY_SLICER.get(), ClayiumItems.CLAY_SLICER.get(), 0.1F,
                RecipeCategory.TOOLS);
        addSmelting(output, "clay_spatula", ClayiumItems.RAW_CLAY_SPATULA.get(), ClayiumItems.CLAY_SPATULA.get(),
                0.1F, RecipeCategory.TOOLS);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ClayiumItems.CLAY_STEEL_PICKAXE.get())
                .pattern("III")
                .pattern(" S ")
                .pattern(" S ")
                .define('I', ClayiumItems.CLAY_STEEL_INGOT.get())
                .define('S', ClayiumItems.DENSE_CLAY_STICK.get())
                .unlockedBy("has_clay_steel_ingot", has(ClayiumItems.CLAY_STEEL_INGOT.get()))
                .save(output, Clayium.id("clay_steel_pickaxe"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ClayiumItems.CLAY_STEEL_SHOVEL.get())
                .pattern("I")
                .pattern("S")
                .pattern("S")
                .define('I', ClayiumItems.CLAY_STEEL_INGOT.get())
                .define('S', ClayiumItems.DENSE_CLAY_STICK.get())
                .unlockedBy("has_clay_steel_ingot", has(ClayiumItems.CLAY_STEEL_INGOT.get()))
                .save(output, Clayium.id("clay_steel_shovel"));
    }

    private static void buildHullAndCircuitRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.RAW_CLAY_MACHINE_HULL.get())
                .pattern("PPP")
                .pattern("PGP")
                .pattern("PPP")
                .define('P', ClayiumItems.LARGE_CLAY_PLATE.get())
                .define('G', ClayiumItems.CLAY_GEAR.get())
                .unlockedBy("has_large_clay_plate", has(ClayiumItems.LARGE_CLAY_PLATE.get()))
                .save(output, Clayium.id("raw_clay_machine_hull"));
        addSmelting(output, "machine_hull_1", ClayiumBlocks.RAW_CLAY_MACHINE_HULL.get(),
                ClayiumBlocks.MACHINE_HULLS.get(1).get(), 0.1F, RecipeCategory.MISC);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.MACHINE_HULLS.get(2).get())
                .pattern("PPP")
                .pattern("PCP")
                .pattern("PPP")
                .define('P', ClayiumItems.DENSE_LARGE_CLAY_PLATE.get())
                .define('C', ClayiumItems.CLAY_CIRCUIT.get())
                .unlockedBy("has_machine_hull_1", has(ClayiumBlocks.MACHINE_HULLS.get(1).get()))
                .save(output, Clayium.id("machine_hull_2"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.MACHINE_HULLS.get(3).get())
                .pattern("PPP")
                .pattern("PCP")
                .pattern("PPP")
                .define('P', ClayiumItems.LARGE_INDUSTRIAL_CLAY_PLATE.get())
                .define('C', ClayiumItems.SIMPLE_CIRCUIT.get())
                .unlockedBy("has_large_industrial_clay_plate", has(ClayiumItems.LARGE_INDUSTRIAL_CLAY_PLATE.get()))
                .save(output, Clayium.id("machine_hull_3"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.ZK60A_ALLOY_HULL.get())
                .pattern("PPP")
                .pattern("PCP")
                .pattern("PPP")
                .define('P', ClayiumItems.LARGE_ZK60A_ALLOY_PLATE.get())
                .define('C', ClayiumItems.PRECISION_CIRCUIT.get())
                .unlockedBy("has_large_zk60a_alloy_plate", has(ClayiumItems.LARGE_ZK60A_ALLOY_PLATE.get()))
                .save(output, Clayium.id("zk60a_alloy_hull"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumItems.CLAY_CIRCUIT.get())
                .pattern("SGS")
                .pattern("RBR")
                .pattern("SGS")
                .define('S', ClayiumItems.DENSE_CLAY_STICK.get())
                .define('G', ClayiumItems.DENSE_CLAY_GEAR.get())
                .define('R', ClayiumItems.DENSE_CLAY_RING.get())
                .define('B', ClayiumItems.CLAY_CIRCUIT_BOARD.get())
                .unlockedBy("has_clay_circuit_board", has(ClayiumItems.CLAY_CIRCUIT_BOARD.get()))
                .save(output, Clayium.id("clay_circuit"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumItems.SIMPLE_CIRCUIT.get())
                .pattern("EEE")
                .pattern("EBE")
                .pattern("EEE")
                .define('E', ClayiumItems.ENERGIZED_CLAY_DUST.get())
                .define('B', ClayiumItems.CLAY_CIRCUIT_BOARD.get())
                .unlockedBy("has_clay_circuit_board", has(ClayiumItems.CLAY_CIRCUIT_BOARD.get()))
                .save(output, Clayium.id("simple_circuit"));
    }

    private static void buildMaterialSmeltingRecipes(RecipeOutput output) {
        addSmelting(output, "silicone_ingot_from_impure_silicon", ClayiumItems.IMPURE_SILICON_INGOT.get(),
                ClayiumItems.SILICONE_INGOT.get(), 0.0F, RecipeCategory.MISC);
        addSmelting(output, "impure_silicon_ingot", ClayiumItems.IMPURE_SILICON_DUST.get(),
                ClayiumItems.IMPURE_SILICON_INGOT.get(), 0.0F, RecipeCategory.MISC);
        addSmelting(output, "silicone_ingot", ClayiumItems.SILICONE_DUST.get(), ClayiumItems.SILICONE_INGOT.get(),
                0.0F, RecipeCategory.MISC);
        addSmelting(output, "silicon_ingot", ClayiumItems.SILICON_DUST.get(), ClayiumItems.SILICON_INGOT.get(), 0.0F,
                RecipeCategory.MISC);
    }

    private static void buildUtilityRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.CLAY_WORK_TABLE.get())
                .pattern("DD")
                .pattern("DD")
                .define('D', ClayiumBlocks.COMPRESSED_CLAYS.get(0).get())
                .unlockedBy("has_dense_clay", has(ClayiumBlocks.COMPRESSED_CLAYS.get(0).get()))
                .save(output, Clayium.id("clay_work_table"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.CHUNK_LOADER.get())
                .pattern("ACA")
                .pattern("C#C")
                .pattern("ACA")
                .define('#', ClayiumBlocks.ZK60A_ALLOY_HULL.get())
                .define('C', ClayiumItems.PRECISION_CIRCUIT.get())
                .define('A', ClayiumItems.ADVANCED_CIRCUIT.get())
                .unlockedBy("has_zk60a_alloy_hull", has(ClayiumBlocks.ZK60A_ALLOY_HULL.get()))
                .save(output, Clayium.id("chunk_loader"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ClayiumItems.FAZY_ITEM_FILTER.get())
                .requires(ClayiumItems.FAZY_ITEM_FILTER.get())
                .unlockedBy("has_fazy_item_filter", has(ClayiumItems.FAZY_ITEM_FILTER.get()))
                .save(output, Clayium.id("fazy_item_filter_reset"));
    }

    private static void addSmelting(RecipeOutput output, String name, ItemLike ingredient, ItemLike result,
                                    float experience, RecipeCategory category) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), category, result, experience, 200)
                .unlockedBy("has_ingredient", has(ingredient))
                .save(output, Clayium.id(name + "_smelting"));
    }
}
