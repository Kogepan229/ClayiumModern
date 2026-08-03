package net.kogepan.clayium.api.gadget;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.items.gadget.ClayGadgetHolderItem;
import net.kogepan.clayium.items.gadget.ClayGadgetItemHandler;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

final class ClayGadgetManager {

    private static final Map<Player, Map<ResourceLocation, TrackedGadget>> ACTIVE = new WeakHashMap<>();

    private ClayGadgetManager() {}

    static synchronized void tick(@NotNull Player player) {
        Map<ResourceLocation, Candidate> resolved = resolve(player);
        Map<ResourceLocation, TrackedGadget> previous = ACTIVE.getOrDefault(player, Map.of());
        Map<ResourceLocation, TrackedGadget> next = new LinkedHashMap<>();

        for (var entry : previous.entrySet()) {
            Candidate candidate = resolved.get(entry.getKey());
            if (candidate == null || !entry.getValue().sameIdentity(candidate)) {
                invokeDeactivated(entry.getValue());
            }
        }

        for (var entry : resolved.entrySet()) {
            TrackedGadget old = previous.get(entry.getKey());
            TrackedGadget current = TrackedGadget.from(entry.getValue(), player);
            if (old == null || !old.sameIdentity(entry.getValue())) {
                invokeActivated(current);
            }
            invokeTick(current);
            next.put(entry.getKey(), current);
        }

        if (next.isEmpty()) {
            ACTIVE.remove(player);
        } else {
            ACTIVE.put(player, next);
        }
    }

    static synchronized void deactivateAll(@NotNull Player player) {
        Map<ResourceLocation, TrackedGadget> gadgets = ACTIVE.remove(player);
        if (gadgets != null) {
            gadgets.values().forEach(ClayGadgetManager::invokeDeactivated);
        }
    }

    @NotNull
    static synchronized List<ActiveClayGadget> getActiveGadgets(@NotNull Player player) {
        Map<ResourceLocation, TrackedGadget> gadgets = ACTIVE.get(player);
        if (gadgets == null) {
            return List.of();
        }
        List<ActiveClayGadget> result = new ArrayList<>(gadgets.size());
        gadgets.values().forEach(tracked -> result.add(tracked.toView()));
        return List.copyOf(result);
    }

    @NotNull
    static synchronized Optional<ActiveClayGadget> findActive(@NotNull Player player,
                                                              @NotNull ResourceLocation category) {
        Map<ResourceLocation, TrackedGadget> gadgets = ACTIVE.get(player);
        return gadgets == null ? Optional.empty() : Optional.ofNullable(gadgets.get(category))
                .map(TrackedGadget::toView);
    }

    @NotNull
    private static Map<ResourceLocation, Candidate> resolve(@NotNull Player player) {
        Inventory inventory = player.getInventory();
        List<Candidate> candidates = new ArrayList<>();
        int holderOrder = 0;
        for (ItemStack holder : inventory.items) {
            collect(holder, holderOrder++, candidates);
        }
        for (ItemStack holder : inventory.offhand) {
            collect(holder, holderOrder++, candidates);
        }
        candidates.sort(Comparator.comparingInt(Candidate::holderOrder)
                .thenComparingInt(Candidate::holderSlot));

        Map<ResourceLocation, Candidate> resolved = new LinkedHashMap<>();
        for (Candidate candidate : candidates) {
            Candidate current = resolved.get(candidate.category());
            if (current == null || candidate.priority() > current.priority()) {
                resolved.put(candidate.category(), candidate);
            }
        }
        return resolved;
    }

    private static void collect(ItemStack holder, int holderOrder, List<Candidate> candidates) {
        if (!holder.is(ClayiumItems.CLAY_GADGET_HOLDER)) {
            return;
        }
        ClayGadgetItemHandler handler = ClayGadgetHolderItem.createHandler(holder);
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            IClayGadget gadget = stack.getCapability(ClayiumCapabilities.CLAY_GADGET);
            if (gadget == null) {
                continue;
            }
            ResourceLocation category = gadget.getCategory(stack);
            candidates.add(new Candidate(category, gadget.getPriority(stack), stack, gadget,
                    handler, holderOrder, slot));
        }
    }

    private static ClayGadgetContext createContext(Candidate candidate, Player player) {
        Item expectedItem = candidate.stack().getItem();
        ResourceLocation expectedCategory = candidate.category();
        return new ClayGadgetContext(player, candidate.stack(), replacement -> {
            ItemStack current = candidate.handler().getStackInSlot(candidate.holderSlot());
            if (current.isEmpty() || current.getItem() != expectedItem) {
                return false;
            }
            if (!replacement.isEmpty()) {
                IClayGadget replacementGadget = replacement.getCapability(ClayiumCapabilities.CLAY_GADGET);
                if (replacementGadget == null || !expectedCategory.equals(replacementGadget.getCategory(replacement))) {
                    return false;
                }
            }
            candidate.handler().setStackInSlot(candidate.holderSlot(), replacement);
            return true;
        });
    }

    private static void invokeActivated(TrackedGadget tracked) {
        try {
            tracked.gadget().onActivated(tracked.context());
        } catch (RuntimeException exception) {
            Clayium.LOGGER.error("Clay gadget activation failed for {}", tracked.stackItem(), exception);
        }
    }

    private static void invokeDeactivated(TrackedGadget tracked) {
        try {
            tracked.gadget().onDeactivated(tracked.context());
        } catch (RuntimeException exception) {
            Clayium.LOGGER.error("Clay gadget deactivation failed for {}", tracked.stackItem(), exception);
        }
    }

    private static void invokeTick(TrackedGadget tracked) {
        try {
            tracked.gadget().tick(tracked.context());
        } catch (RuntimeException exception) {
            Clayium.LOGGER.error("Clay gadget tick failed for {}", tracked.stackItem(), exception);
        }
    }

    private record Candidate(ResourceLocation category, int priority, ItemStack stack,
                             IClayGadget gadget, ClayGadgetItemHandler handler,
                             int holderOrder, int holderSlot) {}

    private record TrackedGadget(ResourceLocation category, int priority, Item stackItem,
                                 IClayGadget gadget, ClayGadgetContext context) {

        static TrackedGadget from(Candidate candidate, Player player) {
            return new TrackedGadget(candidate.category(), candidate.priority(), candidate.stack().getItem(),
                    candidate.gadget(), createContext(candidate, player));
        }

        boolean sameIdentity(Candidate candidate) {
            return category.equals(candidate.category()) && stackItem == candidate.stack().getItem();
        }

        ActiveClayGadget toView() {
            return new ActiveClayGadget(category, priority, context.stack(), gadget);
        }
    }
}
