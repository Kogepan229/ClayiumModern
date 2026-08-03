package net.kogepan.clayium.api.gadget;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public final class ClayGadgetApi {

    private ClayGadgetApi() {}

    /** Returns a stable snapshot of all active gadgets. */
    @NotNull
    public static List<ActiveClayGadget> getActiveGadgets(@NotNull Player player) {
        return ClayGadgetManager.getActiveGadgets(player);
    }

    /** Finds the active gadget in a category. */
    @NotNull
    public static Optional<ActiveClayGadget> findActive(@NotNull Player player,
                                                        @NotNull ResourceLocation category) {
        return ClayGadgetManager.findActive(player, category);
    }

    /** Returns whether the specified item is currently active for the player. */
    public static boolean isActive(@NotNull Player player, @NotNull ItemLike item) {
        return getActiveGadgets(player).stream().anyMatch(active -> active.stack().is(item.asItem()));
    }
}
