package net.kogepan.clayium.client.ldlib.elements;

import net.kogepan.clayium.integration.rei.REIUtils;

import net.minecraft.world.item.crafting.Ingredient;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
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

public class ItemSlotXEI extends ItemSlot {

    public ItemSlotXEI() {
        super();
        style(style -> style.backgroundTexture(MCSprites.RECT_1));
    }

    public ItemSlot xeiRecipeIngredient(IngredientIO io, Ingredient ingredient, int amount) {
        setItem(ingredient.getItems()[0].copyWithCount(amount));
        if (LDLib2.isJeiLoaded()) {
            recipeIngredientJEI(this, io, ingredient, amount);
            recipeSlotJEI(this, ingredient, amount);
        }
        if (LDLib2.isReiLoaded()) {
            recipeIngredientREI(this, io, ingredient, amount);
            recipeSlotREI(this, io, ingredient, amount);
        }
        if (LDLib2.isEmiLoaded()) {
            recipeIngredientEMI(this, io, ingredient, amount);
            recipeSlotEMI(this, ingredient, amount, 1);
        }
        return this;
    }

    private static void recipeIngredientJEI(ItemSlot itemSlot, IngredientIO io, Ingredient ingredient, int amount) {
        List<ITypedIngredient<?>> list = new ArrayList<>();
        for (var stack : ingredient.getItems()) {
            list.add(TypedItemStack.create(stack.copyWithCount(amount)));
        }
        LDLibJEIPlugin.recipeIngredient(itemSlot, io, () -> list);
    }

    private static void recipeSlotJEI(ItemSlot itemSlot, Ingredient ingredient, int amount) {
        LDLibJEIPlugin.recipeSlot(itemSlot, () -> {
            var item = ingredient.getItems()[0];
            return item.isEmpty() ? null : TypedItemStack.create(item);
        }, () -> {
            List<ITypedIngredient<?>> list = new ArrayList<>();
            for (var stack : ingredient.getItems()) {
                list.add(TypedItemStack.create(stack.copyWithCount(amount)));
            }
            return list;
        });
    }

    private static void recipeIngredientREI(ItemSlot itemSlot, IngredientIO io, Ingredient ingredient, int amount) {
        LDLibREIPlugin.recipeIngredient(itemSlot, io, () -> List.of(REIUtils.ofIngredient(ingredient, amount)));
    }

    private static void recipeSlotREI(ItemSlot itemSlot, IngredientIO io, Ingredient ingredient, int amount) {
        LDLibREIPlugin.recipeSlot(itemSlot, io,
                () -> EntryStacks.of(ingredient.getItems()[0]),
                () -> REIUtils.ofIngredient(ingredient, amount));
    }

    private static void recipeIngredientEMI(ItemSlot itemSlot, IngredientIO io, Ingredient ingredient, int amount) {
        LDLibEMIPlugin.recipeIngredient(itemSlot, io, () -> List.of(EmiIngredient.of(ingredient, amount)));
    }

    private static void recipeSlotEMI(ItemSlot itemSlot, Ingredient ingredient, int amount, float chance) {
        LDLibEMIPlugin.recipeSlot(itemSlot, () -> EmiIngredient.of(ingredient, amount).setChance(chance));
    }
}
