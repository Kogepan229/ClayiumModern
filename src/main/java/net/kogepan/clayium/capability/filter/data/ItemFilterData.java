package net.kogepan.clayium.capability.filter.data;

import net.minecraft.world.item.ItemStack;

/** Serializable filter payload stored on a Clay Container face. */
public interface ItemFilterData {

    ItemFilterType<?> type();

    boolean test(ItemStack stack);
}
