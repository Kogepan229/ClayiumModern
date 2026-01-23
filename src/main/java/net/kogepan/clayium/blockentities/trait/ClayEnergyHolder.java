package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.capability.IClayEnergyHolder;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.inventory.FilteredItemHandlerModifiable;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import org.jetbrains.annotations.NotNull;

/**
 * A trait that provides Clay Energy storage functionality.
 * In Modern, ClayEnergy is handled directly as long.
 * See {@link net.kogepan.clayium.utils.CEUtils} for unit conversion constants.
 */
public class ClayEnergyHolder extends ClayContainerTrait implements IClayEnergyHolder {

    private final ClayiumItemStackHandler backingEcSlotHandler;
    private final FilteredItemHandlerModifiable energizedClayItemHandler;

    private long clayEnergy = 0L;
    private int ceSlotStackLimit = 1;

    public ClayEnergyHolder(@NotNull ClayContainerBlockEntity blockEntity) {
        super(blockEntity, "clayEnergyHolder");
        this.backingEcSlotHandler = new ClayiumItemStackHandler(blockEntity, 1) {

            @Override
            public int getSlotLimit(int slot) {
                return ceSlotStackLimit;
            }
        };
        this.energizedClayItemHandler = new FilteredItemHandlerModifiable(
                this.backingEcSlotHandler,
                stack -> {
                    // TODO: Check if stack has ENERGIZED_CLAY capability
                    // For now, we'll need to implement this check when IClayEnergyProvider is available
                    return false; // Placeholder
                });
    }

    @Override
    public void tick() {
        // TODO: Implement energized clay importer when AutoIOTrait pattern is available
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide()) return;

        Level world = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        int limit = 1;

        for (Direction side : Direction.values()) {
            BlockPos neighborPos = pos.relative(side);
            BlockState state = world.getBlockState(neighborPos);
            // TODO: Check for IEnergyStorageUpgradeBlock when available
            // if (state.getBlock() instanceof IEnergyStorageUpgradeBlock upgradeBlock) {
            // limit += upgradeBlock.getExtraStackLimit(world, neighborPos);
            // }
        }

        this.ceSlotStackLimit = limit;
    }

    @Override
    @NotNull
    public IItemHandler getEnergizedClayItemHandler() {
        return this.energizedClayItemHandler;
    }

    @Override
    public long getEnergyStored() {
        return this.clayEnergy;
    }

    @Override
    public boolean drawEnergy(long amount, boolean simulate) {
        if (!hasEnoughEnergy(amount)) return false;
        if (!simulate) {
            this.clayEnergy -= amount;
        }
        return true;
    }

    @Override
    public void addEnergy(long amount) {
        this.clayEnergy += amount;
    }

    /**
     * Tries to consume energized clay from the slot if the current energy is not enough.
     */
    @Override
    public boolean hasEnoughEnergy(long amount) {
        if (this.clayEnergy < amount) {
            tryConsumeEnergizedClay();
        }
        return this.clayEnergy >= amount;
    }

    private void tryConsumeEnergizedClay() {
        ItemStack stack = this.energizedClayItemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return;

        // TODO: Get IClayEnergyProvider capability from stack
        // IClayEnergyProvider ceProvider = stack.getCapability(ClayiumCapabilities.ENERGIZED_CLAY, null);
        // if (ceProvider == null) return;
        // this.clayEnergy += ceProvider.getClayEnergy();
        // this.energizedClayItemHandler.extractItem(0, 1, false);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        tag.putLong("clayEnergy", clayEnergy);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        if (tag.contains("clayEnergy")) {
            clayEnergy = tag.getLong("clayEnergy");
        }
    }
}
