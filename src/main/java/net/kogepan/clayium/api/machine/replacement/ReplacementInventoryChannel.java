package net.kogepan.clayium.api.machine.replacement;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Objects;

/** A stable logical inventory channel whose owned contents move with a machine. */
public record ReplacementInventoryChannel(ResourceLocation id, IItemHandler handler) {

    public ReplacementInventoryChannel {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(handler, "handler");
    }
}
