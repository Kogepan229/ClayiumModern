package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;

import org.jetbrains.annotations.NotNull;

public abstract class ClayContainerTrait {

    @NotNull
    protected final ClayContainerBlockEntity blockEntity;
    public final String id;

    protected ClayContainerTrait(@NotNull ClayContainerBlockEntity blockEntity, String id) {
        this.blockEntity = blockEntity;
        this.id = id;
    }

    abstract public void tick();
}
