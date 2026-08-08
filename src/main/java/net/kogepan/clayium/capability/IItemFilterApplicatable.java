package net.kogepan.clayium.capability;

import net.kogepan.clayium.capability.filter.data.ItemFilterData;

import net.minecraft.core.Direction;

import org.jspecify.annotations.Nullable;

public interface IItemFilterApplicatable {

    void setFilter(Direction side, ItemFilterData filterData);

    @Nullable
    ItemFilterData getFilter(Direction side);

    void clearFilter(Direction side);
}
