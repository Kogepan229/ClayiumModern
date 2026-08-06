package net.kogepan.clayium.integration.jei;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.integration.XEIChemicalMetalSeparatorRecipeCategory;
import net.kogepan.clayium.integration.XEIChemicalMetalSeparatorRecipeCategory.XEIChemicalMetalSeparatorRecipe;
import net.kogepan.clayium.integration.XEIMachineRecipeCategory;
import net.kogepan.clayium.integration.XEISaltExtractorRecipeCategory;
import net.kogepan.clayium.integration.XEISaltExtractorRecipeCategory.XEISaltExtractorRecipe;
import net.kogepan.clayium.integration.xei.JEIRecipeViewer;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public final class ClayiumJEIPlugin implements IModPlugin {

    private static final RecipeType<RecipeHolder<ClayWorkTableRecipe>> CLAY_WORK_TABLE = RecipeType
            .createRecipeHolderType(ClayiumRecipeTypes.CLAY_WORK_TABLE_RECIPE_TYPE.getId());
    private static final RecipeType<XEISaltExtractorRecipe> SALT_EXTRACTOR = new RecipeType<>(
            XEISaltExtractorRecipeCategory.getId(), XEISaltExtractorRecipe.class);
    private static final RecipeType<XEIChemicalMetalSeparatorRecipe> CHEMICAL_METAL_SEPARATOR = new RecipeType<>(
            XEIChemicalMetalSeparatorRecipeCategory.getId(), XEIChemicalMetalSeparatorRecipe.class);
    private static final int CHEMICAL_METAL_SEPARATOR_HEIGHT = 100;

    @Override
    public ResourceLocation getPluginUid() {
        return Clayium.id("jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEIRecipeViewer.onRuntimeAvailable(jeiRuntime);
    }

    @Override
    public void onRuntimeUnavailable() {
        JEIRecipeViewer.onRuntimeUnavailable();
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers helpers = registration.getJeiHelpers();
        List<IRecipeCategory<?>> categories = new ArrayList<>();

        categories.add(new JEIModularUIRecipeCategory<>(
                CLAY_WORK_TABLE,
                holder -> holder.value().createModularUI(),
                categoryTitle(ClayiumRecipeTypes.CLAY_WORK_TABLE_RECIPE_TYPE.getId()),
                createIcon(helpers, ClayiumBlocks.CLAY_WORK_TABLE.get()),
                ClayWorkTableRecipe.WIDTH,
                ClayWorkTableRecipe.HEIGHT,
                RecipeHolder::id));

        for (XEIMachineRecipeCategory category : XEIMachineRecipeCategory.getCategories()) {
            categories.add(new JEIModularUIRecipeCategory<>(
                    machineRecipeType(category),
                    holder -> holder.value().createModularUI(),
                    categoryTitle(category.getId()),
                    createIcon(helpers, category.getIcon().get()),
                    MachineRecipe.WIDTH,
                    MachineRecipe.HEIGHT,
                    RecipeHolder::id));
        }

        categories.add(new JEIModularUIRecipeCategory<>(
                SALT_EXTRACTOR,
                XEISaltExtractorRecipe::createModularUI,
                categoryTitle(XEISaltExtractorRecipeCategory.getId()),
                createIcon(helpers, XEISaltExtractorRecipeCategory.getIcon().get()),
                XEISaltExtractorRecipe.WIDTH,
                XEISaltExtractorRecipe.HEIGHT,
                recipe -> Clayium.id("salt_extractor/" + recipe.tier())));

        categories.add(new JEIModularUIRecipeCategory<>(
                CHEMICAL_METAL_SEPARATOR,
                XEIChemicalMetalSeparatorRecipe::createModularUI,
                categoryTitle(XEIChemicalMetalSeparatorRecipeCategory.getId()),
                createIcon(helpers, XEIChemicalMetalSeparatorRecipeCategory.getIcon()),
                XEIChemicalMetalSeparatorRecipe.WIDTH,
                CHEMICAL_METAL_SEPARATOR_HEIGHT,
                XEIChemicalMetalSeparatorRecipe::getId));

        registration.addRecipeCategories(categories.toArray(IRecipeCategory<?>[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level, "minecraft.level")
                .getRecipeManager();

        List<RecipeHolder<ClayWorkTableRecipe>> workTableRecipes = recipeManager
                .getAllRecipesFor(ClayiumRecipeTypes.CLAY_WORK_TABLE_RECIPE_TYPE.get()).stream()
                .sorted(Comparator.comparingInt(holder -> holder.value().button()))
                .toList();
        registration.addRecipes(CLAY_WORK_TABLE, workTableRecipes);

        for (XEIMachineRecipeCategory category : XEIMachineRecipeCategory.getCategories()) {
            List<RecipeHolder<MachineRecipe>> recipes = recipeManager.getAllRecipesFor(category.getRecipeType())
                    .stream()
                    .sorted(Comparator
                            .<RecipeHolder<MachineRecipe>>comparingInt(holder -> holder.value().recipeTier())
                            .thenComparingLong(holder -> holder.value().adjustedTotalCE())
                            .thenComparing(RecipeHolder::id))
                    .toList();
            registration.addRecipes(machineRecipeType(category), recipes);
        }

        registration.addRecipes(SALT_EXTRACTOR, XEISaltExtractorRecipeCategory.getRecipes());
        registration.addRecipes(CHEMICAL_METAL_SEPARATOR,
                XEIChemicalMetalSeparatorRecipeCategory.getRecipes(recipeManager));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new ClayCraftingBoardJEIRecipeTransferInfo());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ClayiumBlocks.CLAY_CRAFTING_BOARD.get(), RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(ClayiumBlocks.CLAY_WORK_TABLE.get(), CLAY_WORK_TABLE);

        for (XEIMachineRecipeCategory category : XEIMachineRecipeCategory.getCategories()) {
            RecipeType<RecipeHolder<MachineRecipe>> recipeType = machineRecipeType(category);
            for (var workstation : category.getWorkstations()) {
                registration.addRecipeCatalyst(workstation.get(), recipeType);
            }
        }

        for (XEISaltExtractorRecipe recipe : XEISaltExtractorRecipeCategory.getRecipes()) {
            registration.addRecipeCatalyst(recipe.machine().get(), SALT_EXTRACTOR);
        }
        registration.addRecipeCatalyst(ClayiumBlocks.CHEMICAL_METAL_SEPARATOR.get(), CHEMICAL_METAL_SEPARATOR);
    }

    private static RecipeType<RecipeHolder<MachineRecipe>> machineRecipeType(XEIMachineRecipeCategory category) {
        return RecipeType.createRecipeHolderType(category.getId());
    }

    private static Component categoryTitle(ResourceLocation id) {
        return Component.translatable("emi.category." + id.getNamespace() + "." + id.getPath());
    }

    private static IDrawable createIcon(IJeiHelpers helpers, ItemLike itemLike) {
        return helpers.getGuiHelper().createDrawableItemLike(itemLike);
    }
}
