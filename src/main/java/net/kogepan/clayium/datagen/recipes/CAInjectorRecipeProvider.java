package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Original CA Injector recipes supported by content registered in Modern. */
public final class CAInjectorRecipeProvider {

    private static final double ANTIMATTER_GROWTH = 1.2999999523162842D;
    private static final int MAX_MACHINE_TIER = 13;

    private CAInjectorRecipeProvider() {}

    public static void buildRecipes(@NotNull RecipeOutput output) {
        directConversion(output, Blocks.GRAVEL, Blocks.DIRT);
        directConversion(output, Blocks.SAND, Blocks.RED_SAND);
        directConversion(output, Items.REDSTONE, Blocks.OBSIDIAN);
        directConversion(output, ClayiumItems.ORGANIC_CLAY_DUST.get(), Items.WHEAT_SEEDS);
        directConversion(output, Items.WHEAT_SEEDS, Blocks.DANDELION);
        directConversion(output, Blocks.DANDELION, Items.APPLE);
        directConversion(output, Items.APPLE, Items.SUGAR_CANE);
        directConversion(output, Items.SUGAR_CANE, Blocks.OAK_SAPLING);
        directConversion(output, Blocks.OAK_SAPLING, Blocks.OAK_LEAVES);
        directConversion(output, Blocks.OAK_LEAVES, Blocks.OAK_LOG);
        directConversion(output, Blocks.GRASS_BLOCK, Blocks.SHORT_GRASS);
        directConversion(output, Blocks.MYCELIUM, Blocks.BROWN_MUSHROOM);
        directConversion(output, Items.ROTTEN_FLESH, Items.LEATHER);
        directConversion(output, Items.LEATHER, Items.BONE);
        directConversion(output, Items.BONE, Items.SLIME_BALL);

        create(output, "resonating_collector", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(10).get()),
                ClayiumItems.ANTIMATTER_MATTER.toStack(8), new ItemStack(ClayiumBlocks.RESONATING_COLLECTOR.get()),
                originalEnergy(2.0D, 10), 4_000L, 10);
        create(output, "resonator_10", new ItemStack(ClayiumBlocks.MACHINE_HULLS.get(9).get()),
                ClayiumItems.ANTIMATTER_MATTER.toStack(8), new ItemStack(ClayiumBlocks.RESONATORS.get(10).get()),
                originalEnergy(2.0D, 9), 4_000L, 9);
        for (int tier = 11; tier <= 13; tier++) {
            create(output, "resonator_" + tier, new ItemStack(ClayiumBlocks.RESONATORS.get(tier - 1).get(), 16),
                    ClayiumItems.ANTIMATTER_MATTER.toStack(64),
                    new ItemStack(ClayiumBlocks.RESONATORS.get(tier).get()), originalEnergy(2.0D, tier),
                    4_000L, tier);
        }

        upgradeFamily(output, ClayiumBlocks.CLAY_BUFFERS);
        upgradeFamily(output, ClayiumBlocks.MULTITRACK_BUFFERS);
        upgradeFamily(output, ClayiumBlocks.BENDING_MACHINE_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.WIRE_DRAWING_MACHINE_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.PIPE_DRAWING_MACHINE_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.CUTTING_MACHINE_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.LATHE_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.MILLING_MACHINE_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.WATERWHEELS);
        upgradeFamily(output, ClayiumBlocks.COBBLESTONE_GENERATORS);
        upgradeFamily(output, ClayiumBlocks.SALT_EXTRACTOR_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.CLAY_CONDENSER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.ENERGETIC_CLAY_CONDENSER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.GRINDER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.CENTRIFUGE_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.CHEMICAL_REACTOR_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.DECOMPOSER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.ELECTROLYSIS_REACTOR_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.DISTRIBUTOR_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.INSCRIBER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.ASSEMBLER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.MATTER_TRANSFORMER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.CA_INJECTOR_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.CA_CONDENSER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.ALLOY_SMELTER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.SMELTER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.CLAY_INTERFACE_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.LASER_INTERFACE_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.AUTO_CLAY_CONDENSER_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.SOLAR_CLAY_FABRICATOR_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.CLAY_FABRICATOR_BLOCKS);
        upgradeFamily(output, ClayiumBlocks.CLAY_LASER_BLOCKS);
    }

    private static void directConversion(RecipeOutput output, ItemLike input, ItemLike result) {
        String inputName = BuiltInRegistries.ITEM.getKey(input.asItem()).getPath();
        String resultName = BuiltInRegistries.ITEM.getKey(result.asItem()).getPath();
        create(output, inputName + "_to_" + resultName, new ItemStack(input),
                ClayiumItems.ANTIMATTER_MATTER.toStack(), new ItemStack(result), originalEnergy(2.0D, 10), 60L, 10);
    }

    private static void upgradeFamily(RecipeOutput output,
                                      Int2ObjectMap<? extends DeferredBlock<? extends Block>> family) {
        DeferredBlock<? extends Block> previousBlock = null;
        int antimatterAmount = 0;
        for (int tier = 0; tier <= MAX_MACHINE_TIER; tier++) {
            antimatterAmount = Math.min(64, (int) (antimatterAmount + Math.pow(ANTIMATTER_GROWTH, tier)));
            DeferredBlock<? extends Block> currentBlock = family.get(tier);
            if (currentBlock == null) {
                continue;
            }
            if (previousBlock != null) {
                String name = currentBlock.getId().getPath() + "_upgrade";
                create(output, name, new ItemStack(previousBlock.get()),
                        ClayiumItems.ANTIMATTER_MATTER.toStack(antimatterAmount), new ItemStack(currentBlock.get()),
                        originalEnergy(3.0D, tier), 4_000L, tier);
            }
            previousBlock = currentBlock;
            antimatterAmount = 0;
        }
    }

    private static void create(RecipeOutput output, String name, ItemStack input1, ItemStack input2,
                               ItemStack result, long energy, long duration, int tier) {
        MachineRecipe recipe = new MachineRecipe(ClayiumRecipeTypes.CA_INJECTOR_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(input1), ItemIngredientStack.of(input2)), List.of(result),
                duration, energy, tier);
        output.accept(Clayium.id("ca_injector/" + name), recipe, null);
    }

    private static long originalEnergy(double factor, int tier) {
        return (long) (factor * 100.0D * Math.pow(10.0D, tier - 4));
    }
}
