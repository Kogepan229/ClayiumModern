package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Rank-specific outputs selected by the CA Reactor's average hull rank. */
public final class CAReactorRecipeProvider {

    private static final long BASE_DURATION = 300L;
    private static final long BASE_CE_PER_TICK = CEUtils.ceToLong(100L);

    private CAReactorRecipeProvider() {}

    public static void buildRecipes(@NotNull RecipeOutput output) {
        List<ItemStack> results = List.of(
                ClayiumItems.PURE_ANTIMATTER_MATTER.toStack(),
                ClayiumItems.COMPRESSED_PURE_ANTIMATTER_1.toStack(),
                ClayiumItems.COMPRESSED_PURE_ANTIMATTER_2.toStack(),
                ClayiumItems.COMPRESSED_PURE_ANTIMATTER_3.toStack(),
                ClayiumItems.COMPRESSED_PURE_ANTIMATTER_4.toStack(),
                ClayiumItems.COMPRESSED_PURE_ANTIMATTER_5.toStack(),
                ClayiumItems.COMPRESSED_PURE_ANTIMATTER_6.toStack(),
                ClayiumItems.COMPRESSED_PURE_ANTIMATTER_7.toStack(),
                ClayiumItems.COMPRESSED_PURE_ANTIMATTER_8.toStack());

        long duration = BASE_DURATION;
        for (int rankIndex = 0; rankIndex < results.size(); rankIndex++) {
            MachineRecipe recipe = new MachineRecipe(ClayiumRecipeTypes.CA_REACTOR_RECIPE_TYPE.get(),
                    List.of(ItemIngredientStack.of(ClayiumItems.ANTIMATTER_MATTER.toStack())),
                    List.of(results.get(rankIndex)), duration, BASE_CE_PER_TICK, 10);
            output.accept(Clayium.id("ca_reactor/pure_antimatter_rank_" + (rankIndex + 1)), recipe, null);
            duration *= 9L;
        }
    }
}
