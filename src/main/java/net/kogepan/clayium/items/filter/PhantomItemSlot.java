package net.kogepan.clayium.items.filter;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib2.gui.slot.LocalSlot;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;

import java.util.function.Supplier;

final class PhantomItemSlot {

    private PhantomItemSlot() {}

    static ItemSlot create(Supplier<ItemStack> heldStack, int index) {
        ItemSlot itemSlot = new ItemSlot(new LocalSlot() {

            @Override
            public boolean mayPickup(Player player) {
                return false;
            }
        });
        itemSlot.xeiPhantom();
        itemSlot.bind(DataBindingBuilder.itemStack(
                () -> FilterItemHelper.getItem(heldStack.get(), index),
                value -> FilterItemHelper.setItem(heldStack.get(), index, value)).build());
        itemSlot.addEventListener(UIEvents.CLICK, event -> {
            ItemStack carried = event.target.getModularUI().getMenu().getCarried();
            itemSlot.setItem(carried.isEmpty() ? ItemStack.EMPTY : carried.copyWithCount(1));
        });
        return itemSlot;
    }
}
