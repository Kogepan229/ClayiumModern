package net.kogepan.clayium.recipes;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE,
            Clayium.MODID);

    private ClayiumRecipeTypes() {}
}
