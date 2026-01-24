package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.NotNull;

public class ClayWorkTableRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result, int button,
                               int cost) {
        create(output, name, ingredient, result, ItemStack.EMPTY, button, cost);
    }

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result,
                               ItemStack byproduct, int button,
                               int cost) {
        ClayWorkTableRecipe recipe = new ClayWorkTableRecipe(ItemIngredientStack.of(ingredient), result, byproduct,
                button, cost);
        output.accept(Clayium.id("clay_work_table/" + button + "/" + name), recipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput output) {
        // spotless:off

        // 0
        create(output, "clay_stick", Items.CLAY_BALL.getDefaultInstance(), ClayiumItems.CLAY_STICK.toStack(), 0, 4);
        create(output, "large_clay_ball", ClayiumItems.CLAY_PLATE.toStack(3), ClayiumItems.LARGE_CLAY_BALL.toStack(), 0, 40);
        create(output, "clay_needle", ClayiumItems.CLAY_CYLINDER.toStack(), ClayiumItems.CLAY_NEEDLE.toStack(), 0, 3);
        create(output, "clay_cylinder", ClayiumItems.LARGE_CLAY_BALL.toStack(), ClayiumItems.CLAY_CYLINDER.toStack(), 0, 4);
        // 1
        create(output, "clay_blade", ClayiumItems.CLAY_PLATE.toStack(), ClayiumItems.CLAY_BLADE.toStack(), 1, 10);
        create(output, "raw_clay_slicer", ClayiumItems.CLAY_DISC.toStack(), ClayiumItems.RAW_CLAY_SLICER.toStack(), 1, 15);
        create(output, "clay_disc", ClayiumItems.LARGE_CLAY_BALL.toStack(), ClayiumItems.CLAY_DISC.toStack(), 1, 30);
        // 2
        create(output, "clay_blade", ClayiumItems.CLAY_PLATE.toStack(), ClayiumItems.CLAY_BLADE.toStack(), new ItemStack(Items.CLAY_BALL, 2),2, 1);
        create(output, "large_clay_plate", ClayiumItems.CLAY_PLATE.toStack(6), ClayiumItems.LARGE_CLAY_PLATE.toStack(), 2, 10);
        create(output, "raw_clay_slicer", ClayiumItems.CLAY_DISC.toStack(), ClayiumItems.RAW_CLAY_SLICER.toStack(), 2, 2);
        create(output, "clay_disc", ClayiumItems.LARGE_CLAY_BALL.toStack(), ClayiumItems.CLAY_DISC.toStack(), new ItemStack(Items.CLAY_BALL, 2),2, 4);
        // 3
        create(output, "clay_plate", ClayiumItems.CLAY_DISC.toStack(), ClayiumItems.CLAY_PLATE.toStack(), new ItemStack(Items.CLAY_BALL, 2),3, 4);
        // 4
        create(output, "clay_ring", ClayiumItems.CLAY_DISC.toStack(), ClayiumItems.CLAY_RING.toStack(), ClayiumItems.SMALL_CLAY_DISC.toStack(),4, 2);
        create(output, "small_clay_ring", ClayiumItems.SMALL_CLAY_DISC.toStack(), ClayiumItems.SMALL_CLAY_RING.toStack(), ClayiumItems.SHORT_CLAY_STICK.toStack(),4, 1);
        // 5
        create(output, "clay_stick", ClayiumItems.CLAY_PLATE.toStack(), ClayiumItems.CLAY_STICK.toStack(4), 5, 3);
        create(output, "small_clay_disc", ClayiumItems.CLAY_CYLINDER.toStack(), ClayiumItems.SMALL_CLAY_DISC.toStack(8), 5, 7);

        // spotless:on
    }
}
