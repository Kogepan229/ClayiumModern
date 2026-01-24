package net.kogepan.clayium.inventory;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;

import net.minecraft.world.level.Level;

public class NotifiableItemStackHandler extends ClayiumItemStackHandler {

    private final boolean isImport;

    public NotifiableItemStackHandler(ClayContainerBlockEntity blockEntity, boolean isImport) {
        super(blockEntity);
        this.isImport = isImport;
    }

    public NotifiableItemStackHandler(ClayContainerBlockEntity blockEntity, int size, boolean isImport) {
        super(blockEntity, size);
        this.isImport = isImport;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) return;

        if (this.isImport) {
            this.blockEntity.notifyItemInputInventoryChanged();
        } else {
            this.blockEntity.notifyItemOutputInventoryChanged();
        }
    }
}
