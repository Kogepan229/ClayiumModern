package net.kogepan.clayium.client.ldlib.elements;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import com.lowdragmc.lowdraglib2.gui.slot.LocalSlot;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import org.jetbrains.annotations.NotNull;

public class PhantomItemSlot {

    public static ItemSlot create(IItemHandlerModifiable itemHandler, int index) {
        ItemSlot itemSlot = new ItemSlot(new LocalSlot() {

            @Override
            public boolean mayPickup(@NotNull Player player) {
                return false;
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return itemHandler.isItemValid(index, stack);
            }
        });

        itemSlot.xeiPhantom();
        itemSlot.bind(DataBindingBuilder.itemStack(() -> itemHandler.getStackInSlot(index),
                itemStack -> itemHandler.setStackInSlot(index, itemStack)).build());
        itemSlot.addEventListener(UIEvents.CLICK, (e) -> {
            ItemSlot slot = (ItemSlot) e.target;
            ItemStack carried = e.target.getModularUI().getMenu().getCarried();
            if (itemHandler.isItemValid(index, carried)) {
                ItemStack configItem = carried.copy();
                configItem.setCount(1);
                slot.setItem(configItem);
            } else if (carried.isEmpty()) {
                slot.setItem(ItemStack.EMPTY);
            }
        });
        return itemSlot;
    }
}
