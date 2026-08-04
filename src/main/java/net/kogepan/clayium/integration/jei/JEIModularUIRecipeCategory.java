package net.kogepan.clayium.integration.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.gui.ui.utils.IModularUIProvider;
import com.lowdragmc.lowdraglib2.integration.xei.jei.ModularUIRecipeCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;

import java.util.function.Function;

/**
 * A reusable JEI category backed by an LDLib2 modular UI.
 *
 * @param <T> the recipe display type
 */
public final class JEIModularUIRecipeCategory<T> extends ModularUIRecipeCategory<T> {

    private final RecipeType<T> recipeType;
    private final Component title;
    private final IDrawable icon;
    private final int width;
    private final int height;
    private final Function<T, ResourceLocation> registryName;

    public JEIModularUIRecipeCategory(RecipeType<T> recipeType, IModularUIProvider<T> uiProvider, Component title,
                                      IDrawable icon, int width, int height,
                                      Function<T, ResourceLocation> registryName) {
        super(uiProvider);
        this.recipeType = recipeType;
        this.title = title;
        this.icon = icon;
        this.width = width;
        this.height = height;
        this.registryName = registryName;
    }

    @Override
    public RecipeType<T> getRecipeType() {
        return this.recipeType;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public ResourceLocation getRegistryName(T recipe) {
        return this.registryName.apply(recipe);
    }
}
