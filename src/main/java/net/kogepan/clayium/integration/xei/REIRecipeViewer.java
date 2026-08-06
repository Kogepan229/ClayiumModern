package net.kogepan.clayium.integration.xei;

import net.minecraft.resources.ResourceLocation;

import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

final class REIRecipeViewer {

    private REIRecipeViewer() {}

    static boolean hasRecipeCategory(ResourceLocation categoryId) {
        return CategoryRegistry.getInstance().tryGet(CategoryIdentifier.of(categoryId)).isPresent();
    }

    static boolean openRecipeCategory(ResourceLocation categoryId) {
        CategoryIdentifier<?> category = CategoryIdentifier.of(categoryId);
        if (CategoryRegistry.getInstance().tryGet(category).isEmpty()) {
            return false;
        }
        return ViewSearchBuilder.builder()
                .addCategory(category)
                .setPreferredOpenedCategory(category)
                .open();
    }
}
