package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.inventory.DroppableItemStackHandler;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import lombok.Getter;

public class ClayCraftingBoardBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Getter
    @Persisted(subPersisted = true)
    private final DroppableItemStackHandler inventory = new DroppableItemStackHandler(9) {

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public ClayCraftingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(ClayiumBlockEntityTypes.CLAY_CRAFTING_BOARD_BLOCK_ENTITY.get(), pos, state);
    }
}
