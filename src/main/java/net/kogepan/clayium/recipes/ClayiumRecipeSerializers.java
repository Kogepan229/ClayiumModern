package net.kogepan.clayium.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;
import net.kogepan.clayium.recipes.serializers.ClayWorkTableRecipeSerializer;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClayiumRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, Clayium.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ClayWorkTableRecipe>> CLAY_WORK_TABLE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS
            .register("clay_work_table", ClayWorkTableRecipeSerializer::new);
}
