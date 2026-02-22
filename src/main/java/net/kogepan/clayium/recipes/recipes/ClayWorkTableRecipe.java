package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.ClayWorkTableButton;
import net.kogepan.clayium.client.ldlib.elements.ItemSlotXEI;
import net.kogepan.clayium.client.ldlib.elements.LargeItemSlot;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.client.ldlib.textures.ClayWorkTableButtonTextures;
import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.integration.xei.IngredientIO;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClayWorkTableRecipe(@NotNull ItemIngredientStack ingredient,
                                  @NotNull ItemStack result,
                                  @NotNull ItemStack byproduct,
                                  int button,
                                  int cost)
        implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return false;
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider provider) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return this.result;
    }

    @Override
    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ClayiumRecipeSerializers.CLAY_WORK_TABLE_RECIPE_SERIALIZER.get();
    }

    @Override
    @NotNull
    public RecipeType<?> getType() {
        return ClayiumRecipeTypes.CLAY_WORK_TABLE_RECIPE_TYPE.get();
    }

    public static final int WIDTH = 174;
    public static final int HEIGHT = 70;

    private UIElement createWorkTableButton(ClayWorkTableButtonTextures.ButtonTexture texture, int index) {
        UIElement uiElement = new UIElement();
        uiElement.layout(layout -> layout.height(16 + 9).marginTop(-9).justifyContent(AlignContent.FLEX_END));

        if (this.button == index) {
            uiElement.addChild(new CLabel().setText(String.valueOf(this.cost))
                    .textStyle(style -> style.textAlignHorizontal(Horizontal.CENTER))
                    .layout(layout -> layout.width(16)));
        }

        ClayWorkTableButton button = new ClayWorkTableButton(texture);
        button.setActive(this.button == index);

        return uiElement.addChild(button);
    }

    public ModularUI createModularUI() {
        var root = new UIElement().layout(layout -> layout
                .width(WIDTH)
                .height(HEIGHT)
                .paddingAll(6)
                .justifyContent(AlignContent.CENTER));

        ItemSlotXEI toolSlot = new ItemSlotXEI();

        switch (this.button) {
            case 2 -> toolSlot.xeiRecipeIngredient(IngredientIO.CATALYST, Ingredient.of(ClayiumItems.CLAY_ROLLING_PIN),
                    1);
            case 3, 5 -> toolSlot.xeiRecipeIngredient(IngredientIO.CATALYST,
                    Ingredient.of(ClayiumItems.CLAY_SPATULA, ClayiumItems.CLAY_SLICER), 1);
            case 4 -> toolSlot.xeiRecipeIngredient(IngredientIO.CATALYST, Ingredient.of(ClayiumItems.CLAY_SPATULA), 1);
        }

        root.addChild(new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW).paddingHorizontal(4))
                .addChild(new LargeItemSlot(new ItemSlotXEI().xeiRecipeIngredient(IngredientIO.INPUT,
                        this.ingredient.getIngredient(), this.ingredient.getAmount()))
                        .layout(layout -> layout.marginTop(15)))
                .addChild(new UIElement()
                        .layout(layout -> layout.flexGrow(1).paddingHorizontal(0).alignItems(AlignItems.CENTER))
                        .addChild(new UIElement()
                                .layout(layout -> layout.flexDirection(FlexDirection.ROW)
                                        .justifyContent(AlignContent.CENTER))
                                .addChild(toolSlot))
                        .addChild(new ProgressArrow().layout(layout -> layout.width(80)))
                        .addChild(new UIElement()
                                .layout(layout -> layout.flexDirection(FlexDirection.ROW)
                                        .justifyContent(AlignContent.CENTER).marginTop(5))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON1, 0))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON2, 1))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON3, 2))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON4, 3))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON5, 4))
                                .addChild(createWorkTableButton(ClayWorkTableButtonTextures.BUTTON6, 5))))
                .addChild(new UIElement()
                        .layout(layout -> layout.marginTop(15).gapAll(3).alignItems(AlignItems.CENTER))
                        .addChild(new LargeItemSlot(new ItemSlot().setItem(this.result)
                                .xeiRecipeIngredient(IngredientIO.OUTPUT).xeiRecipeSlot()))
                        .addChild(new ItemSlot().setItem(this.byproduct).xeiRecipeIngredient(IngredientIO.OUTPUT)
                                .xeiRecipeSlot())));
        return new ModularUI(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))));
    }
}
