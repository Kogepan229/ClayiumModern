package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.IItemFilter;
import net.kogepan.clayium.capability.filter.UnlocalizedNameItemFilter;
import net.kogepan.clayium.client.ldlib.elements.CLabel;

import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib2.gui.factory.HeldItemUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import org.appliedenergistics.yoga.YogaJustify;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Filter item: one regex string matched against stack's description id (translation key).
 */
public class UnlocalizedNameItemFilterItem extends ItemFilterBase {

    public UnlocalizedNameItemFilterItem() {
        super();
    }

    @Override
    @NotNull
    public IItemFilter createFilter(@NotNull ItemStack stack) {
        String filterString = FilterItemHelper.getFilterString(stack);
        return new UnlocalizedNameItemFilter(filterString);
    }

    @Override
    @NotNull
    protected ModularUI createFilterUI(HeldItemUIMenuType.HeldItemUIHolder holder) {
        var root = new UIElement().layout(layout -> layout.paddingAll(6).setJustifyContent(YogaJustify.CENTER))
                .addClass("panel_bg");
        root.addChild(new CLabel().setText(holder.itemStack.getHoverName().getString()));

        var filterStringBinding = DataBindingBuilder
                .string(() -> FilterItemHelper.getFilterString(holder.player.getItemInHand(holder.hand)),
                        value -> FilterItemHelper.setFilterString(holder.player.getItemInHand(holder.hand), value))
                .build();
        var textField = new TextField().bind(filterStringBinding);
        root.addChild(textField);

        root.addChild(new CLabel().setText("container.inventory"));
        root.addChild(new InventorySlots());
        return new ModularUI(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))),
                holder.player);
    }
}
