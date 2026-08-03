package net.kogepan.clayium.integration;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.client.ldlib.elements.XEIItemDisplay;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.WeightedItemStack;
import net.kogepan.clayium.recipes.recipes.WeightedMachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class XEIChemicalMetalSeparatorRecipeCategory {

    private static final ResourceLocation ID = Clayium.id("chemical_metal_separator");

    private XEIChemicalMetalSeparatorRecipeCategory() {}

    public static ResourceLocation getId() {
        return ID;
    }

    public static Block getIcon() {
        return ClayiumBlocks.CHEMICAL_METAL_SEPARATOR.get();
    }

    public static List<XEIChemicalMetalSeparatorRecipe> getRecipes(RecipeManager recipeManager) {
        return recipeManager.getAllRecipesFor(ClayiumRecipeTypes.CHEMICAL_METAL_SEPARATOR_RECIPE_TYPE.get()).stream()
                .sorted(Comparator
                        .comparingInt((RecipeHolder<WeightedMachineRecipe> holder) -> holder.value().recipeTier())
                        .thenComparingLong(holder -> holder.value().adjustedTotalCE())
                        .thenComparing(RecipeHolder::id))
                .map(XEIChemicalMetalSeparatorRecipe::new)
                .toList();
    }

    public record XEIChemicalMetalSeparatorRecipe(RecipeHolder<WeightedMachineRecipe> holder) {

        public static final int WIDTH = 204;
        private static final int BASE_HEIGHT = 62;
        private static final int OUTPUT_COLUMNS = 10;
        private static final int OUTPUT_SLOT_SIZE = 18;
        private static final int OUTPUT_GAP = 1;
        private static final int OUTPUT_ROW_STEP = OUTPUT_SLOT_SIZE + OUTPUT_GAP;
        private static final int PROGRESS_ARROW_WIDTH = 20;

        public ResourceLocation getId() {
            return this.holder.id();
        }

        public int getDisplayHeight() {
            int outputRows = (this.holder.value().weightedOutputs().size() + OUTPUT_COLUMNS - 1) / OUTPUT_COLUMNS;
            return BASE_HEIGHT + outputRows * OUTPUT_ROW_STEP;
        }

        public ModularUI createModularUI() {
            WeightedMachineRecipe recipe = this.holder.value();
            var root = new UIElement().layout(layout -> layout
                    .width(WIDTH)
                    .height(getDisplayHeight())
                    .paddingAll(6)
                    .alignItems(AlignItems.CENTER)
                    .justifyContent(AlignContent.CENTER));

            UIElement inputRow = new UIElement().layout(layout -> layout
                    .height(22)
                    .gapAll(6)
                    .flexDirection(FlexDirection.ROW)
                    .alignItems(AlignItems.CENTER)
                    .justifyContent(AlignContent.CENTER));
            for (ItemIngredientStack input : recipe.inputs()) {
                inputRow.addChild(XEIItemDisplay.input(input.getIngredient(), input.getAmount()));
            }
            inputRow.addChild(new ProgressArrow().bindDataSource(SupplierDataSource.of(() -> {
                ClientLevel level = Minecraft.getInstance().level;
                final long clientTick = level != null ? level.getGameTime() : 0;
                final int threshold = PROGRESS_ARROW_WIDTH * 2;
                return (float) (clientTick % threshold) / threshold;
            })).layout(layout -> layout.width(PROGRESS_ARROW_WIDTH)));
            root.addChild(inputRow);

            List<WeightedItemStack> sortedOutputs = recipe.weightedOutputs().stream()
                    .sorted(Comparator.comparingInt(WeightedItemStack::weight).reversed()
                            .thenComparing(output -> BuiltInRegistries.ITEM.getKey(output.result().getItem())))
                    .toList();
            UIElement outputGrid = new UIElement().layout(layout -> layout
                    .marginTop(3)
                    .width(OUTPUT_COLUMNS * OUTPUT_SLOT_SIZE + (OUTPUT_COLUMNS - 1) * OUTPUT_GAP)
                    .gapAll(OUTPUT_GAP)
                    .flexDirection(FlexDirection.COLUMN)
                    .alignItems(AlignItems.CENTER));
            for (int rowStart = 0; rowStart < sortedOutputs.size(); rowStart += OUTPUT_COLUMNS) {
                UIElement outputRow = new UIElement().layout(layout -> layout
                        .widthPercent(100)
                        .height(OUTPUT_SLOT_SIZE)
                        .gapAll(OUTPUT_GAP)
                        .flexDirection(FlexDirection.ROW));
                int rowEnd = Math.min(rowStart + OUTPUT_COLUMNS, sortedOutputs.size());
                for (int outputIndex = rowStart; outputIndex < rowEnd; outputIndex++) {
                    WeightedItemStack output = sortedOutputs.get(outputIndex);
                    float chance = (float) output.weight() / recipe.totalWeight();
                    outputRow.addChild(XEIItemDisplay.output(
                            Ingredient.of(output.result()), output.result().getCount(), chance));
                }
                outputGrid.addChild(outputRow);
            }
            root.addChild(outputGrid);

            long adjustedDuration = recipe.adjustedDuration();
            root.addChild(new UIElement().layout(layout -> layout.marginTop(3).alignItems(AlignItems.CENTER))
                    .addChild(new CLabel().setText(Component.translatable("xei.clayium.tier", recipe.recipeTier())))
                    .addChild(new CLabel().setText(String.format(Locale.ROOT, "%s/t ✕ %st = %s",
                            CEUtils.formatCE(recipe.cePerTick()), adjustedDuration,
                            CEUtils.formatCE(recipe.cePerTick() * adjustedDuration)))));

            return new ModularUI(
                    UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))));
        }
    }
}
