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

/** Original Clay Reactor recipes supported by the currently ported content. */
public final class ClayReactorRecipeProvider {

    private ClayReactorRecipeProvider() {}

    private static void create(RecipeOutput output, String name, ItemStack input, ItemStack result,
                               double energyFactor, long duration, int tier) {
        create(output, name, List.of(ItemIngredientStack.of(input)), result, energyFactor, duration, tier);
    }

    private static void create(RecipeOutput output, String name, ItemStack input1, ItemStack input2,
                               ItemStack result, double energyFactor, long duration, int tier) {
        create(output, name, List.of(ItemIngredientStack.of(input1), ItemIngredientStack.of(input2)), result,
                energyFactor, duration, tier);
    }

    private static void create(RecipeOutput output, String name, List<ItemIngredientStack> inputs, ItemStack result,
                               double energyFactor, long duration, int tier) {
        long cePerTick = CEUtils.ceToLong(energyFactor * Math.pow(10.0D, tier - 7));
        MachineRecipe recipe = new MachineRecipe(ClayiumRecipeTypes.CLAY_REACTOR_RECIPE_TYPE.get(),
                inputs, List.of(result), duration, cePerTick, tier);
        output.accept(Clayium.id("clay_reactor/" + name), recipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput output) {
        // spotless:off

        create(output, "clayium_from_lithium", ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(8), ClayiumItems.LITHIUM_DUST.toStack(4), ClayiumItems.CLAYIUM_DUST.toStack(8), 10.0D, 50_000L, 7);
        create(output, "clayium_from_hafnium", ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(8), ClayiumItems.HAFNIUM_DUST.toStack(), ClayiumItems.CLAYIUM_DUST.toStack(8), 10.0D, 500_000L, 7);
        create(output, "clayium_from_barium", ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(8), ClayiumItems.BARIUM_DUST.toStack(), ClayiumItems.CLAYIUM_DUST.toStack(8), 3.0D, 5_000_000L, 7);
        create(output, "clayium_from_strontium", ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(8), ClayiumItems.STRONTIUM_DUST.toStack(), ClayiumItems.CLAYIUM_DUST.toStack(8), 1.0D, 50_000_000L, 7);
        create(output, "ultimate_alloy", ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(), ClayiumItems.IMPURE_ULTIMATE_ALLOY_INGOT.toStack(), ClayiumItems.ULTIMATE_ALLOY_INGOT.toStack(), 10.0D, 1_000_000_000L, 8);
        create(output, "excited_clay", ClayiumItems.ENERGIZED_CLAY_DUST.toStack(8), ClayiumItems.LITHIUM_DUST.toStack(), ClayiumItems.EXCITED_CLAY_DUST.toStack(4), 1.0D, 2_000_000L, 7);
        create(output, "organic_clay", ClayiumItems.ORGANIC_CLAY_DUST.toStack(), ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(), ClayiumItems.ORGANIC_CLAY_DUST.toStack(2), 1.0D, 1_000_000_000_000L, 10);
        create(output, "organic_clay_from_clay_soul", ClayiumItems.CLAY_SOUL.toStack(), ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(), ClayiumItems.ORGANIC_CLAY_DUST.toStack(2), 1.0D, 100_000_000_000_000L, 11);
        create(output, "antimatter_seed", ClayiumItems.CLAYIUM_INGOT.toStack(), ClayiumItems.ANTIMATTER_SEED.toStack(), 1.0D, 200_000_000_000_000L, 9);

        create(output, "clay_core", ClayiumItems.INTEGRATED_CIRCUIT.toStack(6), ClayiumItems.EXCITED_CLAY_DUST.toStack(), ClayiumItems.CLAY_CORE.toStack(), 10.0D, 8_000_000L, 7);
        create(output, "clay_brain", ClayiumItems.CLAY_CORE.toStack(6), ClayiumItems.EXCITED_CLAY_DUST.toStack(12), ClayiumItems.CLAY_BRAIN.toStack(), 10.0D, 4_000_000_000L, 8);
        create(output, "clay_spirit", ClayiumItems.CLAY_BRAIN.toStack(6), ClayiumItems.EXCITED_CLAY_DUST.toStack(32), ClayiumItems.CLAY_SPIRIT.toStack(), 10.0D, 10_000_000_000_000L, 9);
        create(output, "clay_soul", ClayiumItems.CLAY_SPIRIT.toStack(6), ClayiumItems.ANTIMATTER_MATTER.toStack(4), ClayiumItems.CLAY_SOUL.toStack(), 10.0D, 10_000_000_000_000L, 10);
        create(output, "clay_anima", ClayiumItems.CLAY_SOUL.toStack(6), ClayiumItems.ANTIMATTER_MATTER.toStack(16), ClayiumItems.CLAY_ANIMA.toStack(), 30.0D, 100_000_000_000_000L, 11);
        create(output, "clay_psyche", ClayiumItems.CLAY_ANIMA.toStack(6), ClayiumItems.ANTIMATTER_MATTER.toStack(64), ClayiumItems.CLAY_PSYCHE.toStack(), 90.0D, 1_000_000_000_000_000L, 12);

        create(output, "teleportation_parts", ClayiumItems.PURE_ANTIMATTER_MATTER.toStack(8), ClayiumItems.INTEGRATED_CIRCUIT.toStack(), ClayiumItems.TELEPORTATION_PARTS.toStack(), 1.0D, 10_000_000_000_000L, 11);
        create(output, "redstone", ClayiumItems.IMPURE_REDSTONE_DUST.toStack(), new ItemStack(Items.REDSTONE), 0.1D, 2_000L, 7);
        create(output, "glowstone_dust", ClayiumItems.IMPURE_GLOWSTONE_DUST.toStack(), new ItemStack(Items.GLOWSTONE_DUST), 0.1D, 2_000L, 7);
        create(output, "dirt", new ItemStack(Blocks.GRAVEL), ClayiumItems.ORGANIC_CLAY_DUST.toStack(), new ItemStack(Blocks.DIRT), 1.0D, 100L, 7);

        create(output, "clay_fabricator_mk1", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(8).get()), new ItemStack(ClayiumBlocks.SOLAR_CLAY_FABRICATOR_BLOCKS.get(7).get()), new ItemStack(ClayiumBlocks.CLAY_FABRICATOR_BLOCKS.get(8).get()), 3.0D, 100_000_000L, 8);
        create(output, "clay_fabricator_mk2", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(9).get()), new ItemStack(ClayiumBlocks.SOLAR_CLAY_FABRICATOR_BLOCKS.get(7).get()), new ItemStack(ClayiumBlocks.CLAY_FABRICATOR_BLOCKS.get(9).get()), 3.0D, 100_000_000_000L, 9);
        create(output, "clay_fabricator_mk3", new ItemStack(ClayiumBlocks.CLAY_FABRICATOR_BLOCKS.get(9).get(), 64), new ItemStack(ClayiumBlocks.OVERCLOCKERS.get(13).get(), 16), new ItemStack(ClayiumBlocks.CLAY_FABRICATOR_BLOCKS.get(13).get()), 10.0D, 1_000_000_000_000_000_000L, 13);

        create(output, "overclocker_10", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(10).get()), new ItemStack(ClayiumBlocks.RESONATORS.get(10).get(), 8), new ItemStack(ClayiumBlocks.OVERCLOCKERS.get(10).get()), 5.0D, 10_000_000_000_000L, 10);
        create(output, "overclocker_11", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(11).get(), 4), new ItemStack(ClayiumBlocks.RESONATORS.get(11).get(), 16), new ItemStack(ClayiumBlocks.OVERCLOCKERS.get(11).get()), 5.0D, 100_000_000_000_000L, 11);
        create(output, "overclocker_12", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(12).get(), 16), new ItemStack(ClayiumBlocks.RESONATORS.get(12).get(), 32), new ItemStack(ClayiumBlocks.OVERCLOCKERS.get(12).get()), 5.0D, 1_000_000_000_000_000L, 12);
        create(output, "overclocker_13", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(13).get(), 64), new ItemStack(ClayiumBlocks.RESONATORS.get(13).get(), 64), new ItemStack(ClayiumBlocks.OVERCLOCKERS.get(13).get()), 5.0D, 1_000_000_000_000_000L, 13);

        // spotless:on
    }
}
