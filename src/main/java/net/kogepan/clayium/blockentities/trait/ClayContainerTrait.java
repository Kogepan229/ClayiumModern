package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

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

    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {}

    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {}
}
