package net.kogepan.clayium;

import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry for items that use the 3-layer dust model (dust_base, dust_dark, dust_light)
 * with per-layer tint colors. Used by client ItemColors and by datagen for item models.
 */
public final class ColoredDustTints {

    /** Number of texture layers (and tint indices) for colored dusts. */
    public static final int LAYER_COUNT = 3;

    private static final Map<DeferredHolder<Item, ?>, int[]> ENTRIES = new LinkedHashMap<>();

    private ColoredDustTints() {}

    /**
     * Registers an item to use the 3-layer dust model with the given colors.
     * Colors are ARGB (e.g. 0xFFRRGGBB). Layer 0 = base, 1 = dark, 2 = light.
     */
    public static void register(DeferredHolder<Item, ?> holder, int base, int dark, int light) {
        ENTRIES.put(holder, new int[] { base, dark, light });
    }

    /** Returns whether the given item holder is registered as a colored dust. */
    public static boolean isColoredDust(DeferredHolder<Item, ?> holder) {
        return ENTRIES.containsKey(holder);
    }

    /**
     * Returns the tint color for the given holder and layer index, or 0xFFFFFFFF
     * if not registered or index out of range.
     */
    public static int getColor(DeferredHolder<Item, ?> holder, int tintIndex) {
        int[] colors = ENTRIES.get(holder);
        if (colors == null || tintIndex < 0 || tintIndex >= colors.length) {
            return 0xFFFFFFFF;
        }
        return colors[tintIndex];
    }

    /** Returns an unmodifiable view of all registered item holders. */
    public static Collection<DeferredHolder<Item, ?>> getRegisteredHolders() {
        return Collections.unmodifiableCollection(ENTRIES.keySet());
    }

    static {
        // Same colors as antimatter / pure antimatter / OPA plates (from ColoredPlateTints)
        int[] antimatter = { 0xFF0000EB, 0xFF000000, 0xFFFFFFFF };
        int[] pureAntimatter = { 0xFFFF32FF, 0xFF000000, 0xFFFFFFFF };
        int[] octuplePureAntimatter = { 0xFF960000, 0xFFC8C800, 0xFFFFFFFF };

        register(ClayiumItems.ANTIMATTER_DUST, antimatter[0], antimatter[1], antimatter[2]);
        register(ClayiumItems.PURE_ANTIMATTER_DUST, pureAntimatter[0], pureAntimatter[1], pureAntimatter[2]);
        register(ClayiumItems.OCTUPLE_PURE_ANTIMATTER_DUST, octuplePureAntimatter[0], octuplePureAntimatter[1],
                octuplePureAntimatter[2]);
    }
}
