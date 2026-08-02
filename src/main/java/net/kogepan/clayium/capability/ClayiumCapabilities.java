package net.kogepan.clayium.capability;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;

import net.neoforged.neoforge.capabilities.ItemCapability;

import org.jspecify.annotations.Nullable;

public final class ClayiumCapabilities {

    public static final ItemCapability<ItemFilterData, @Nullable Void> ITEM_FILTER_DATA = ItemCapability
            .createVoid(Clayium.id("item_filter_data"), ItemFilterData.class);

    private ClayiumCapabilities() {}
}
