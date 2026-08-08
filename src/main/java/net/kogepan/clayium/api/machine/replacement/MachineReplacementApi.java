package net.kogepan.clayium.api.machine.replacement;

import net.minecraft.world.level.block.entity.BlockEntityType;

import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public final class MachineReplacementApi {

    private static final Map<BlockEntityType<?>, MachineReplacementAdapter> ADAPTERS = new IdentityHashMap<>();

    private MachineReplacementApi() {}

    public static synchronized void register(BlockEntityType<?> type, MachineReplacementAdapter adapter) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(adapter, "adapter");
        MachineReplacementAdapter previous = ADAPTERS.putIfAbsent(type, adapter);
        if (previous != null) {
            throw new IllegalStateException("A machine replacement adapter is already registered for " + type);
        }
    }

    @Nullable
    public static synchronized MachineReplacementAdapter find(BlockEntityType<?> type) {
        return ADAPTERS.get(type);
    }
}
