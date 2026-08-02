package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Objects;

public abstract class ClayContainerTrait {

    protected final ClayContainerBlockEntity blockEntity;
    private final String id;

    protected ClayContainerTrait(ClayContainerBlockEntity blockEntity, String id) {
        this.blockEntity = Objects.requireNonNull(blockEntity);
        this.id = Objects.requireNonNull(id);
    }

    public final String id() {
        return this.id;
    }

    public void tick() {}

    public void onLoad() {}

    public void onChunkUnloaded() {}

    public void onRemoved() {}

    public void notifyItemInputInventoryChanged() {}

    public void notifyItemOutputInventoryChanged() {}

    public void saveAdditional(ValueOutput output) {}

    public void loadAdditional(ValueInput input) {}

    public void saveForUpdate(ValueOutput output) {}

    public void loadForUpdate(ValueInput input) {}
}
