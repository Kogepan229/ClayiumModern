package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.filter.data.FuzzyFilterData;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.items.filter.component.FilterSlotsData;
import net.kogepan.clayium.registries.ClayiumDataComponents;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib2.gui.factory.HeldItemUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;

public final class FazyItemFilterItem extends ItemFilterBase {

    public FazyItemFilterItem(Item.Properties properties) {
        super(properties.component(ClayiumDataComponents.FILTER_SLOTS.get(), FilterSlotsData.EMPTY), 7);
    }

    @Override
    public ItemFilterData createFilterData(ItemStack stack) {
        return new FuzzyFilterData(FilterItemHelper.readItems(stack));
    }

    @Override
    public ModularUI createUI(HeldItemUIMenuType.HeldItemUIHolder holder) {
        UIElement root = SimpleItemFilterItem.createRoot(holder);
        root.addChild(SimpleItemFilterItem.createSlotGrid(holder));
        SimpleItemFilterItem.addPlayerInventory(root);
        return SimpleItemFilterItem.createModularUI(holder, root);
    }
}
