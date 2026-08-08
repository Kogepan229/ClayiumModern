package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.capability.filter.data.SimpleFilterData;
import net.kogepan.clayium.items.filter.component.FilterSlotsData;
import net.kogepan.clayium.registries.ClayiumDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib2.gui.factory.HeldItemUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Toggle;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;

import java.util.List;

public final class SimpleItemFilterItem extends ItemFilterBase {

    public SimpleItemFilterItem(Item.Properties properties) {
        super(properties
                .component(ClayiumDataComponents.FILTER_SLOTS.get(), FilterSlotsData.EMPTY)
                .component(ClayiumDataComponents.FILTER_WHITELIST.get(), true), 5);
    }

    @Override
    public ItemFilterData createFilterData(ItemStack stack) {
        return new SimpleFilterData(FilterItemHelper.readItems(stack), FilterItemHelper.getWhitelist(stack));
    }

    @Override
    public ModularUI createUI(HeldItemUIMenuType.HeldItemUIHolder holder) {
        UIElement root = createRoot(holder);
        root.addChild(createSlotGrid(holder));

        Toggle blacklistToggle = new Toggle().noText();
        blacklistToggle.bind(DataBindingBuilder
                .bool(
                        () -> !FilterItemHelper.getWhitelist(holder.player.getItemInHand(holder.hand)),
                        value -> FilterItemHelper.setWhitelist(holder.player.getItemInHand(holder.hand), !value))
                .build());
        UIElement toggleRow = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.ROW)
                .alignItems(AlignItems.CENTER)
                .justifyContent(AlignContent.CENTER));
        toggleRow.addChild(blacklistToggle);
        toggleRow.addChild(createLabel(Component.translatable("gui.clayium.item_filter.deny_list")));
        root.addChild(toggleRow);
        addPlayerInventory(root);
        return createModularUI(holder, root);
    }

    static UIElement createRoot(HeldItemUIMenuType.HeldItemUIHolder holder) {
        UIElement root = new UIElement().layout(layout -> layout
                .paddingAll(6)
                .alignItems(AlignItems.CENTER)
                .justifyContent(AlignContent.CENTER))
                .addClass("panel_bg");
        root.addChild(createLabel(holder.itemStack.getHoverName()));
        return root;
    }

    static UIElement createSlotGrid(HeldItemUIMenuType.HeldItemUIHolder holder) {
        UIElement grid = new UIElement();
        for (int row = 0; row < 2; row++) {
            UIElement slotRow = new UIElement().layout(layout -> layout
                    .flexDirection(FlexDirection.ROW)
                    .justifyContent(AlignContent.CENTER));
            for (int column = 0; column < 5; column++) {
                slotRow.addChild(PhantomItemSlot.create(
                        () -> holder.player.getItemInHand(holder.hand),
                        row * 5 + column));
            }
            grid.addChild(slotRow);
        }
        return grid;
    }

    static Label createLabel(Component text) {
        Label label = new Label();
        label.textStyle(style -> style.textColor(0xff404040).textShadow(false));
        label.setText(text);
        return label;
    }

    static void addPlayerInventory(UIElement root) {
        root.addChild(createLabel(Component.translatable("container.inventory")));
        root.addChild(new InventorySlots());
    }

    static ModularUI createModularUI(HeldItemUIMenuType.HeldItemUIHolder holder, UIElement root) {
        return new ModularUI(
                UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))),
                holder.player);
    }
}
