package net.kogepan.clayium.integration.xei;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.util.UISoundUtils;

public final class XEIRecipeViewer {

    private XEIRecipeViewer() {}

    public static <T extends UIElement> T makeRecipeCategoryClickable(T element, ResourceLocation categoryId) {
        element.addEventListener(UIEvents.CLICK, event -> {
            if (event.button == 0 && openRecipeCategory(categoryId)) {
                UISoundUtils.playButtonClickSound();
                event.stopPropagation();
            }
        });
        element.addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
            if (canOpenRecipeCategory(categoryId)) {
                HoverTooltips tooltips = event.hoverTooltips != null ? event.hoverTooltips : HoverTooltips.empty();
                event.hoverTooltips = tooltips.append(Component.translatable("gui.clayium.xei.recipes"));
            }
        });
        return element;
    }

    private static boolean canOpenRecipeCategory(ResourceLocation categoryId) {
        if (!Platform.isClient()) {
            return false;
        }
        if (Platform.isModLoaded("emi") && EMIRecipeViewer.hasRecipeCategory(categoryId)) {
            return true;
        }
        if (Platform.isModLoaded("roughlyenoughitems") && REIRecipeViewer.hasRecipeCategory(categoryId)) {
            return true;
        }
        return Platform.isModLoaded("jei") && JEIRecipeViewer.hasRecipeCategory(categoryId);
    }

    private static boolean openRecipeCategory(ResourceLocation categoryId) {
        if (!Platform.isClient()) {
            return false;
        }
        if (Platform.isModLoaded("emi") && EMIRecipeViewer.openRecipeCategory(categoryId)) {
            return true;
        }
        if (Platform.isModLoaded("roughlyenoughitems") && REIRecipeViewer.openRecipeCategory(categoryId)) {
            return true;
        }
        return Platform.isModLoaded("jei") && JEIRecipeViewer.openRecipeCategory(categoryId);
    }
}
