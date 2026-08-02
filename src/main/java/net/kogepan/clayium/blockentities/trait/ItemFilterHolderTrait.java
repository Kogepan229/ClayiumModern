package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.registries.ClayiumFilterTypes;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

public final class ItemFilterHolderTrait extends ClayContainerTrait {

    public static final String TRAIT_ID = "itemFilterHolder";

    private static final String FILTER_PREFIX = "filter";
    private static final String FILTER_SIDES_KEY = "filterSides";

    private final Map<Direction, ItemFilterData> filters = new EnumMap<>(Direction.class);
    private final EnumSet<Direction> clientFilterSides = EnumSet.noneOf(Direction.class);

    public ItemFilterHolderTrait(ClayContainerBlockEntity blockEntity) {
        super(blockEntity, TRAIT_ID);
    }

    public void setFilter(Direction side, ItemFilterData data) {
        Direction checkedSide = Objects.requireNonNull(side);
        this.filters.put(checkedSide, Objects.requireNonNull(data));
        this.clientFilterSides.add(checkedSide);
        this.blockEntity.notifyTransferConfigurationChanged();
    }

    public @Nullable ItemFilterData getFilter(Direction side) {
        return this.filters.get(side);
    }

    public void clearFilter(Direction side) {
        if (this.filters.remove(side) != null) {
            this.clientFilterSides.remove(side);
            this.blockEntity.notifyTransferConfigurationChanged();
        }
    }

    public boolean hasFilterClientOnly(Direction side) {
        return this.clientFilterSides.contains(side);
    }

    public void rotateSides(Rotation rotation) {
        Map<Direction, ItemFilterData> previousFilters = new EnumMap<>(this.filters);
        EnumSet<Direction> previousClientFilterSides = EnumSet.copyOf(this.clientFilterSides);
        this.filters.clear();
        this.clientFilterSides.clear();
        previousFilters.forEach((side, filter) -> this.filters.put(rotation.rotate(side), filter));
        previousClientFilterSides.forEach(side -> this.clientFilterSides.add(rotation.rotate(side)));
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        for (Direction side : Direction.values()) {
            ItemFilterData filter = this.filters.get(side);
            if (filter != null) {
                output.store(
                        FILTER_PREFIX + side.get3DDataValue(),
                        ClayiumFilterTypes.FILTER_DATA_CODEC,
                        filter);
            }
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        this.filters.clear();
        for (Direction side : Direction.values()) {
            input.read(
                    FILTER_PREFIX + side.get3DDataValue(),
                    ClayiumFilterTypes.FILTER_DATA_CODEC)
                    .ifPresent(filter -> this.filters.put(side, filter));
        }
        this.clientFilterSides.clear();
        this.clientFilterSides.addAll(this.filters.keySet());
    }

    @Override
    public void saveForUpdate(ValueOutput output) {
        int filterSides = 0;
        for (Direction side : this.filters.keySet()) {
            filterSides |= 1 << side.get3DDataValue();
        }
        output.putInt(FILTER_SIDES_KEY, filterSides);
    }

    @Override
    public void loadForUpdate(ValueInput input) {
        int filterSides = input.getIntOr(FILTER_SIDES_KEY, 0);
        this.clientFilterSides.clear();
        for (Direction side : Direction.values()) {
            if ((filterSides & 1 << side.get3DDataValue()) != 0) {
                this.clientFilterSides.add(side);
            }
        }
    }
}
