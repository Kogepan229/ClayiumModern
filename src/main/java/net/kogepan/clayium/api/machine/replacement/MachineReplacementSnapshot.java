package net.kogepan.clayium.api.machine.replacement;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record MachineReplacementSnapshot(CompoundTag state, List<InventorySnapshot> inventories) {

    public MachineReplacementSnapshot {
        state = state.copy();
        inventories = inventories.stream().map(InventorySnapshot::copy).toList();
    }

    @Override
    public CompoundTag state() {
        return this.state.copy();
    }

    @Override
    public List<InventorySnapshot> inventories() {
        return this.inventories.stream().map(InventorySnapshot::copy).toList();
    }

    /** Slot-preserving contents of one logical inventory channel. */
    public record InventorySnapshot(ResourceLocation id, List<ItemStack> stacks) {

        public InventorySnapshot {
            stacks = stacks.stream().map(ItemStack::copy).toList();
        }

        private InventorySnapshot copy() {
            return new InventorySnapshot(this.id, this.stacks);
        }

        @Override
        public List<ItemStack> stacks() {
            return this.stacks.stream().map(ItemStack::copy).toList();
        }
    }
}
