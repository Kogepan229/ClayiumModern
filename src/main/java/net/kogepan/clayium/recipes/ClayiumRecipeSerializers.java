package net.kogepan.clayium.recipes;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, Clayium.MODID);

    private ClayiumRecipeSerializers() {}
}
