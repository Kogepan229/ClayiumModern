package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.IItemFilter;
import net.kogepan.clayium.capability.filter.SimpleItemFilter;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.PhantomItemSlot;

import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib2.gui.factory.HeldItemUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Toggle;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.appliedenergistics.yoga.YogaJustify;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Filter item: list of items + whitelist/blacklist. Match by exact stack (or nested filter).
 */
public class SimpleItemFilterItem extends ItemFilterBase {

    public static final int FILTER_SIZE_X = 5;
    public static final int FILTER_SIZE_Y = 2;

    public SimpleItemFilterItem() {
        super();
    }

    @Override
    @NotNull
    public IItemFilter createFilter(@NotNull ItemStack stack) {
        List<ItemStack> stacks = FilterItemHelper.readItems(stack);
        boolean whitelist = FilterItemHelper.getWhitelist(stack, true);
        return new SimpleItemFilter(stacks, whitelist);
    }

    @Override
    @NotNull
    protected ModularUI createFilterUI(HeldItemUIMenuType.HeldItemUIHolder holder) {
        var handler = new FilterSlotItemHandler(() -> holder.player.getItemInHand(holder.hand));
        var root = new UIElement().layout(layout -> layout.paddingAll(6).setJustifyContent(YogaJustify.CENTER))
                .addClass("panel_bg");
        root.addChild(new CLabel().setText(holder.itemStack.getHoverName().getString()));

        var slotContainer = new UIElement();
        for (int row = 0; row < FILTER_SIZE_Y; row++) {
            var slotRow = new UIElement().layout(
                    layout -> layout.flexDirection(YogaFlexDirection.ROW).setJustifyContent(YogaJustify.CENTER));
            slotContainer.addChild(slotRow);
            for (int col = 0; col < FILTER_SIZE_X; col++) {
                int index = row * FILTER_SIZE_X + col;
                slotRow.addChild(PhantomItemSlot.create(handler, index));
            }
        }

        // Toggle: checked = Blacklist, unchecked = Allow (bind to inverse of whitelist)
        var blacklistToggleBinding = DataBindingBuilder
                .bool(() -> !FilterItemHelper.getWhitelist(holder.player.getItemInHand(holder.hand), true),
                        value -> FilterItemHelper.setWhitelist(holder.player.getItemInHand(holder.hand), !value))
                .build();

        var buttonContainer = new UIElement().layout(
                layout -> layout.flexDirection(YogaFlexDirection.ROW).setJustifyContent(YogaJustify.SPACE_BETWEEN));
        var toggleButton = new Toggle().noText().bind(blacklistToggleBinding).layout(layout -> layout.marginLeft(35));
        toggleButton.addChild(new CLabel().setText("Blacklist").layout(layout -> layout.marginLeft(2)));
        buttonContainer.addChild(toggleButton);

        root.addChild(slotContainer);
        root.addChild(buttonContainer);
        root.addChild(new CLabel().setText("container.inventory"));
        root.addChild(new InventorySlots());
        return new ModularUI(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))),
                holder.player);
    }
}
