package net.kogepan.clayium.items.filter;

import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.capability.filter.data.UnlocalizedNameFilterData;
import net.kogepan.clayium.registries.ClayiumDataComponents;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib2.gui.factory.HeldItemUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;

public final class UnlocalizedNameItemFilterItem extends ItemFilterBase {

    public UnlocalizedNameItemFilterItem(Item.Properties properties) {
        super(properties.component(ClayiumDataComponents.FILTER_STRING.get(), ""), 6);
    }

    @Override
    public ItemFilterData createFilterData(ItemStack stack) {
        return new UnlocalizedNameFilterData(FilterItemHelper.getFilterString(stack));
    }

    @Override
    public ModularUI createUI(HeldItemUIMenuType.HeldItemUIHolder holder) {
        UIElement root = SimpleItemFilterItem.createRoot(holder);
        root.addChild(new TextField().bind(DataBindingBuilder.string(
                () -> FilterItemHelper.getFilterString(holder.player.getItemInHand(holder.hand)),
                value -> FilterItemHelper.setFilterString(holder.player.getItemInHand(holder.hand), value)).build()));
        SimpleItemFilterItem.addPlayerInventory(root);
        return SimpleItemFilterItem.createModularUI(holder, root);
    }
}
