package net.kogepan.clayium.integration.xei;

import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.runtime.IJeiRuntime;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class JEIRecipeViewer {

    @Nullable
    private static IJeiRuntime runtime;

    private JEIRecipeViewer() {}

    public static void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    public static void onRuntimeUnavailable() {
        runtime = null;
    }

    static boolean hasRecipeCategory(ResourceLocation categoryId) {
        IJeiRuntime currentRuntime = runtime;
        return currentRuntime != null && currentRuntime.getRecipeManager().getRecipeType(categoryId).isPresent();
    }

    static boolean openRecipeCategory(ResourceLocation categoryId) {
        IJeiRuntime currentRuntime = runtime;
        if (currentRuntime == null) {
            return false;
        }
        return currentRuntime.getRecipeManager().getRecipeType(categoryId)
                .map(recipeType -> {
                    currentRuntime.getRecipesGui().showTypes(List.of(recipeType));
                    return true;
                })
                .orElse(false);
    }
}
