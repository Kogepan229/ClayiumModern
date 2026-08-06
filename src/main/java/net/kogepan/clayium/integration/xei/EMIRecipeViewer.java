package net.kogepan.clayium.integration.xei;

import net.minecraft.resources.ResourceLocation;

import dev.emi.emi.api.EmiApi;

final class EMIRecipeViewer {

    private EMIRecipeViewer() {}

    static boolean hasRecipeCategory(ResourceLocation categoryId) {
        return EmiApi.getRecipeManager().getCategories().stream()
                .anyMatch(category -> category.getId().equals(categoryId));
    }

    static boolean openRecipeCategory(ResourceLocation categoryId) {
        return EmiApi.getRecipeManager().getCategories().stream()
                .filter(category -> category.getId().equals(categoryId))
                .findFirst()
                .map(category -> {
                    EmiApi.displayRecipeCategory(category);
                    return true;
                })
                .orElse(false);
    }
}
