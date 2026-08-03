package net.kogepan.clayium.client.ldlib.elements;

import net.kogepan.clayium.integration.rei.REIUtils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.MCSprites;
import com.lowdragmc.lowdraglib2.integration.xei.IngredientIO;
import com.lowdragmc.lowdraglib2.integration.xei.emi.LDLibEMIPlugin;
import com.lowdragmc.lowdraglib2.integration.xei.jei.LDLibJEIPlugin;
import com.lowdragmc.lowdraglib2.integration.xei.rei.LDLibREIPlugin;
import dev.emi.emi.api.stack.EmiIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.library.ingredients.itemStacks.TypedItemStack;

import java.util.ArrayList;
import java.util.List;

public final class XEIItemDisplay extends UIElement {

    private XEIItemDisplay(IngredientIO io, Ingredient ingredient, int amount, float chance) {
        ItemStack[] ingredientStacks = ingredient.getItems();
        ItemStack[] displayStacks = new ItemStack[ingredientStacks.length];
        for (int index = 0; index < ingredientStacks.length; index++) {
            displayStacks[index] = ingredientStacks[index].copyWithCount(amount);
        }

        layout(layout -> layout.width(18).height(18).paddingAll(1));
        style(style -> style.backgroundTexture(MCSprites.RECT_1));
        addChild(new UIElement()
                .layout(layout -> layout.width(16).height(16))
                .style(style -> style.backgroundTexture(new ItemStackTexture(displayStacks))));

        if (LDLib2.isJeiLoaded()) {
            recipeIngredientJEI(this, io, ingredient, amount);
            recipeSlotJEI(this, ingredient, amount);
        }
        if (LDLib2.isReiLoaded()) {
            recipeIngredientREI(this, io, ingredient, amount);
            recipeSlotREI(this, io, ingredient, amount);
        }
        if (LDLib2.isEmiLoaded()) {
            recipeIngredientEMI(this, io, ingredient, amount, chance);
            recipeSlotEMI(this, ingredient, amount, chance);
        }
    }

    public static XEIItemDisplay input(Ingredient ingredient, int amount) {
        return new XEIItemDisplay(IngredientIO.INPUT, ingredient, amount, 1.0F);
    }

    public static XEIItemDisplay output(Ingredient ingredient, int amount, float chance) {
        return new XEIItemDisplay(IngredientIO.OUTPUT, ingredient, amount, chance);
    }

    private static void recipeIngredientJEI(UIElement element, IngredientIO io, Ingredient ingredient, int amount) {
        List<ITypedIngredient<?>> list = new ArrayList<>();
        for (var stack : ingredient.getItems()) {
            list.add(TypedItemStack.create(stack.copyWithCount(amount)));
        }
        LDLibJEIPlugin.recipeIngredient(element, io, () -> list);
    }

    private static void recipeSlotJEI(UIElement element, Ingredient ingredient, int amount) {
        LDLibJEIPlugin.recipeSlot(element, () -> {
            var items = ingredient.getItems();
            return items.length == 0 || items[0].isEmpty() ? null : TypedItemStack.create(items[0]);
        }, () -> {
            List<ITypedIngredient<?>> list = new ArrayList<>();
            for (var stack : ingredient.getItems()) {
                list.add(TypedItemStack.create(stack.copyWithCount(amount)));
            }
            return list;
        });
    }

    private static void recipeIngredientREI(UIElement element, IngredientIO io, Ingredient ingredient, int amount) {
        LDLibREIPlugin.recipeIngredient(element, io, () -> List.of(REIUtils.ofIngredient(ingredient, amount)));
    }

    private static void recipeSlotREI(UIElement element, IngredientIO io, Ingredient ingredient, int amount) {
        LDLibREIPlugin.recipeSlot(element, io,
                () -> {
                    var items = ingredient.getItems();
                    return items.length == 0 ? EntryStacks.of(ItemStack.EMPTY) : EntryStacks.of(items[0]);
                },
                () -> REIUtils.ofIngredient(ingredient, amount));
    }

    private static void recipeIngredientEMI(UIElement element, IngredientIO io, Ingredient ingredient, int amount,
                                            float chance) {
        LDLibEMIPlugin.recipeIngredient(element, io,
                () -> List.of(EmiIngredient.of(ingredient, amount).setChance(chance)));
    }

    private static void recipeSlotEMI(UIElement element, Ingredient ingredient, int amount, float chance) {
        LDLibEMIPlugin.recipeSlot(element, () -> EmiIngredient.of(ingredient, amount).setChance(chance));
    }
}
