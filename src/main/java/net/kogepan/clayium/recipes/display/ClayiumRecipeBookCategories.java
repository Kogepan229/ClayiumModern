package net.kogepan.clayium.recipes.display;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumRecipeBookCategories {

    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister
            .create(Registries.RECIPE_BOOK_CATEGORY, Clayium.MODID);

    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> CLAYIUM = RECIPE_BOOK_CATEGORIES
            .register("clayium", RecipeBookCategory::new);

    private ClayiumRecipeBookCategories() {}
}
