package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Original Matter Transformer recipes whose inputs and outputs exist in Modern. */
public final class MatterTransformerRecipeProvider {

    private MatterTransformerRecipeProvider() {}

    public static void buildRecipes(@NotNull RecipeOutput output) {
        create(output, ClayiumItems.ZINC_INGOT.toStack(), new ItemStack(Items.COPPER_INGOT), originalEnergy(20.0D, 8),
                200L, 8);
        create(output, new ItemStack(Items.DIAMOND), new ItemStack(Items.AMETHYST_SHARD),
                originalEnergy(2.0D, 10), 200L, 10);
        create(output, new ItemStack(Items.AMETHYST_SHARD), new ItemStack(Items.EMERALD),
                originalEnergy(13.0D, 10), 200L, 11);

        chain(output, new ItemLike[] { Blocks.COBBLESTONE, Blocks.NETHERRACK, Blocks.END_STONE },
                null, new int[] { 0, 9, 11 }, 20L);
        chain(output, new ItemLike[] { Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS,
                Blocks.CHISELED_STONE_BRICKS }, null, new int[] { 0, 8, 8 }, 20L);
        chain(output, new ItemLike[] { Blocks.DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.MYCELIUM,
                Blocks.SOUL_SAND }, null, new int[] { 0, 7, 8, 9, 10 }, 20L);

        ItemLike[] woods = { Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG,
                Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG };
        ItemLike[] leaves = { Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES,
                Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES };
        ItemLike[] saplings = { Blocks.OAK_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.BIRCH_SAPLING,
                Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING };
        int[] woodTiers = { 0, 7, 7, 8, 8, 8 };
        chain(output, woods, null, woodTiers, 80L);
        chain(output, leaves, null, woodTiers, 20L);
        chain(output, saplings, null, woodTiers, 20L);

        chain(output, new ItemLike[] { Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS,
                Items.COCOA_BEANS, Items.NETHER_WART }, null, new int[] { 0, 8, 8, 8, 8 }, 20L);
        chain(output, new ItemLike[] { Items.WHEAT, Items.CARROT, Items.POTATO }, null,
                new int[] { 0, 8, 8 }, 20L);
        chain(output, new ItemLike[] { Blocks.SHORT_GRASS, Blocks.FERN, Blocks.DEAD_BUSH, Blocks.VINE,
                Blocks.LILY_PAD }, null, new int[] { 0, 7, 7, 8, 9 }, 20L);
        chain(output, new ItemLike[] { Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM,
                Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP,
                Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.SUNFLOWER, Blocks.LILAC, Blocks.TALL_GRASS,
                Blocks.LARGE_FERN, Blocks.ROSE_BUSH, Blocks.PEONY }, null,
                new int[] { 0, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8 }, 20L);
        chain(output, new ItemLike[] { Items.SUGAR_CANE, Blocks.CACTUS }, null, new int[] { 0, 8 }, 20L);

        chain(output, new ItemLike[] { Items.ROTTEN_FLESH, Items.PORKCHOP, Items.BEEF, Items.CHICKEN },
                new int[] { 64, 2, 2, 1 }, new int[] { 0, 9, 9, 9 }, 200L);
        chain(output, new ItemLike[] { Items.LEATHER, Blocks.WHITE_WOOL, Items.FEATHER },
                new int[] { 1, 4, 16 }, new int[] { 0, 9, 9 }, 80L);
        chain(output, new ItemLike[] { Items.BONE, Items.BLAZE_ROD, Items.ENDER_PEARL, Items.NETHER_STAR,
                Blocks.DRAGON_EGG }, new int[] { 262_144, 4_096, 1_024, 64, 1 },
                new int[] { 0, 9, 9, 12, 12 }, 200L);
        chain(output, new ItemLike[] { Items.SLIME_BALL, Items.EGG, Items.INK_SAC, Items.SPIDER_EYE,
                Items.GHAST_TEAR }, null, new int[] { 0, 8, 8, 9, 10 }, 100L);
        chain(output, new ItemLike[] { Items.WITHER_SKELETON_SKULL, Items.SKELETON_SKULL, Items.ZOMBIE_HEAD,
                Items.CREEPER_HEAD, Items.PLAYER_HEAD }, null, new int[] { 0, 11, 11, 11, 12 }, 1_000L);
        chain(output, new ItemLike[] { Blocks.GRAVEL, Items.FLINT }, null, new int[] { 0, 7 }, 1_000L);
    }

    private static void chain(RecipeOutput output, ItemLike[] items, int[] stackSizes, int[] tiers, long duration) {
        for (int index = 1; index < items.length; index++) {
            int inputCount = 1;
            int outputCount = 1;
            if (stackSizes != null) {
                int divisor = greatestCommonDivisor(stackSizes[index - 1], stackSizes[index]);
                inputCount = stackSizes[index - 1] / divisor;
                outputCount = stackSizes[index] / divisor;
            }
            create(output, new ItemStack(items[index - 1], inputCount), new ItemStack(items[index], outputCount),
                    originalEnergy(1.0D, tiers[index]), duration, tiers[index]);
        }
    }

    private static int greatestCommonDivisor(int left, int right) {
        int x = Math.max(left, right);
        int y = Math.min(left, right);
        while (y != 0) {
            int remainder = x % y;
            x = y;
            y = remainder;
        }
        return x;
    }

    private static void create(RecipeOutput output, ItemStack input, ItemStack result, long energy,
                               long duration, int tier) {
        MachineRecipe recipe = new MachineRecipe(ClayiumRecipeTypes.MATTER_TRANSFORMER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(input)), List.of(result), duration, energy, tier);
        String inputName = BuiltInRegistries.ITEM.getKey(input.getItem()).getPath();
        String outputName = BuiltInRegistries.ITEM.getKey(result.getItem()).getPath();
        output.accept(Clayium.id("matter_transformer/" + inputName + "_to_" + outputName), recipe, null);
    }

    private static long originalEnergy(double factor, int tier) {
        return (long) (factor * 100.0D * Math.pow(10.0D, tier - 4));
    }
}
