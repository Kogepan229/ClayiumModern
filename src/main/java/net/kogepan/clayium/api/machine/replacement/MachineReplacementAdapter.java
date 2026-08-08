package net.kogepan.clayium.api.machine.replacement;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public interface MachineReplacementAdapter {

    ResourceLocation familyId();

    default MachineReplacementDecision canReplace(MachineReplacementContext context) {
        return MachineReplacementDecision.allow();
    }

    default MachineReplacementDecision validateInstalled(MachineReplacementContext context) {
        return MachineReplacementDecision.allow();
    }

    default CompoundTag saveState(ClayContainerBlockEntity machine, HolderLookup.Provider provider) {
        return machine.saveWithoutMetadata(provider);
    }

    default void loadState(ClayContainerBlockEntity machine, CompoundTag state, HolderLookup.Provider provider) {
        machine.loadWithComponents(state, provider);
    }

    default List<ReplacementInventoryChannel> getInventoryChannels(ClayContainerBlockEntity machine) {
        List<ReplacementInventoryChannel> channels = new ArrayList<>();
        Set<IItemHandler> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        int index = 0;
        for (IItemHandler handler : machine.getReplacementInventoryHandlers()) {
            if (visited.add(handler)) {
                channels.add(new ReplacementInventoryChannel(
                        Clayium.id("replacement_inventory_" + index++), handler));
            }
        }
        return List.copyOf(channels);
    }

    default void afterReplacement(MachineReplacementContext context) {
        context.target().finishMachineReplacement();
    }
}
