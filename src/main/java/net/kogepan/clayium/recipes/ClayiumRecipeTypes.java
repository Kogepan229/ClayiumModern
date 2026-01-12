package net.kogepan.clayium.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClayiumRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE,
            Clayium.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ClayWorkTableRecipe>> CLAY_WORK_TABLE_RECIPE_TYPE = RECIPE_TYPES
            .register("clay_work_table", RecipeType::simple);
}
