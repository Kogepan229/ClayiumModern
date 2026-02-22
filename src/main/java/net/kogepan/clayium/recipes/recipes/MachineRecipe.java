package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.ItemSlotXEI;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.client.ldlib.textures.XEITextures;
import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.inputs.MachineRecipeInput;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.integration.xei.IngredientIO;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record MachineRecipe(
                            @NotNull SimpleMachineRecipeType<MachineRecipe> recipeType,
                            @NotNull List<ItemIngredientStack> inputs,
                            @NotNull List<ItemStack> outputs,
                            long duration,
                            long cePerTick,
                            int recipeTier)
        implements Recipe<MachineRecipeInput> {

    /**
     * Checks if this recipe matches the given input and machine tier.
     *
     * @param input The recipe input containing item stacks
     * @param level The level (not used in machine recipes)
     * @return true if the recipe matches
     */
    @Override
    public boolean matches(@NotNull MachineRecipeInput input, @NotNull Level level) {
        return matches(input, Integer.MAX_VALUE);
    }

    /**
     * Checks if this recipe matches the given input and machine tier.
     *
     * @param input       The recipe input containing item stacks
     * @param machineTier The tier of the machine
     * @return true if the recipe matches
     */
    public boolean matches(@NotNull MachineRecipeInput input, int machineTier) {
        if (this.recipeTier > machineTier) {
            return false;
        }

        List<ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            inputItems.add(input.getItem(i));
        }
        return matchesItems(inputItems);
    }

    /**
     * Checks if the input items match the recipe requirements.
     * This implementation follows the original Recipe class logic where ingredients
     * are matched in order, and each ingredient must match a different input slot.
     *
     * @param inputItems The recipe input containing item stacks
     * @return true if all ingredients match
     */
    public boolean matchesItems(@NotNull List<ItemStack> inputItems) {
        for (ItemIngredientStack ingredient : this.inputs) {
            boolean isMatched = false;
            for (int i = 0; i < inputItems.size(); i++) {
                ItemStack itemStack = inputItems.get(i);

                // Check if this slot matches the ingredient (both item type and amount)
                if (ingredient.test(itemStack) && itemStack.getCount() >= ingredient.getAmount()) {
                    isMatched = true;
                    break;
                }
            }
            // If one of the ingredients is not matched, return false
            if (!isMatched) {
                return false;
            }
        }
        // All ingredients are matched
        return true;
    }

    /**
     * Assembles the recipe result.
     *
     * @param input    The recipe input
     * @param provider The holder lookup provider
     * @return The first output item stack
     */
    @Override
    @NotNull
    public ItemStack assemble(@NotNull MachineRecipeInput input, @NotNull HolderLookup.Provider provider) {
        if (outputs.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return outputs.getFirst().copy();
    }

    /**
     * Checks if the recipe can be crafted in the given dimensions.
     * Machine recipes don't have dimension constraints.
     *
     * @param width  The width (not used)
     * @param height The height (not used)
     * @return true
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    /**
     * Gets the result item stack.
     *
     * @param provider The holder lookup provider
     * @return The first output item stack
     */
    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        if (outputs.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return outputs.getFirst();
    }

    /**
     * Gets all output item stacks.
     *
     * @return A copy of all output item stacks
     */
    @NotNull
    public List<ItemStack> copyOutputs() {
        return outputs.stream().map(ItemStack::copy).toList();
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ClayiumRecipeSerializers.MACHINE_RECIPE_SERIALIZER.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return recipeType;
    }

    public static final int WIDTH = 174;
    public static final int HEIGHT = 70;
    private static final int PROGRESS_ARROW_WIDTH = 20;

    public ModularUI createModularUI() {
        var root = new UIElement().layout(layout -> layout
                .width(WIDTH)
                .height(HEIGHT)
                .paddingAll(6)
                .justifyContent(AlignContent.CENTER));

        root.addChild(new UIElement().layout(layout -> layout.width(28).height(11))
                .style(style -> style.backgroundTexture(XEITextures.BADGE)));

        UIElement inputContainer = new UIElement()
                .layout(layout -> layout.flexDirection(FlexDirection.ROW).width(69).height(18)
                        .justifyContent(AlignContent.FLEX_END))
                .style(style -> style.background(XEITextures.SLOT));
        UIElement outputContainer = new UIElement()
                .layout(layout -> layout.flexDirection(FlexDirection.ROW).width(69).height(18))
                .style(style -> style.background(XEITextures.SLOT));

        for (ItemIngredientStack input : this.inputs) {
            inputContainer.addChild(new ItemSlotXEI().xeiRecipeIngredient(IngredientIO.INPUT,
                    input.getIngredient(), input.getAmount()).style(style -> style.background(IGuiTexture.EMPTY)));
        }

        for (ItemStack output : this.outputs) {
            outputContainer.addChild(new ItemSlotXEI().xeiRecipeIngredient(IngredientIO.OUTPUT,
                    Ingredient.of(output), output.getCount()).style(style -> style.background(IGuiTexture.EMPTY)));
        }

        root.addChild(new UIElement()
                .layout(layout -> layout.marginTop(3).gapAll(3).flexDirection(FlexDirection.ROW))
                .addChild(inputContainer)
                .addChild(new ProgressArrow().bindDataSource(SupplierDataSource
                        .of(() -> {
                            ClientLevel level = Minecraft.getInstance().level;
                            final long clientTick = level != null ? level.getGameTime() : 0;
                            final int threshold = PROGRESS_ARROW_WIDTH * 2;
                            return (float) (clientTick % threshold) / threshold;
                        }))
                        .layout(layout -> layout.width(PROGRESS_ARROW_WIDTH)))
                .addChild(outputContainer));

        root.addChild(new UIElement().layout(layout -> layout.marginTop(3))
                .addChild(new CLabel().setText(Component.translatable("xei.clayium.tier", this.recipeTier)))
                .addChild(new CLabel().setText(String.format("%s/t ✕ %st = %s", CEUtils.formatCE(this.cePerTick),
                        this.duration, CEUtils.formatCE(this.cePerTick * this.duration)))));

        return new ModularUI(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))));
    }
}
