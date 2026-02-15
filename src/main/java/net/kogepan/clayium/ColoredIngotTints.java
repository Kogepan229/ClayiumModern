package net.kogepan.clayium;

import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry for items that use the 3-layer ingot model (base, dark, light) with
 * per-layer tint colors. Used by client ItemColors and by datagen for item
 * models. Add entries here to support more colored ingot-style items.
 */
public final class ColoredIngotTints {

    /** Number of texture layers (and tint indices) for colored ingots. */
    public static final int LAYER_COUNT = 3;

    private static final Map<DeferredHolder<Item, ?>, int[]> ENTRIES = new LinkedHashMap<>();

    private ColoredIngotTints() {}

    /**
     * Registers an item to use the 3-layer ingot model with the given colors.
     * Colors are ARGB (e.g. 0xFFRRGGBB). Layer 0 = base, 1 = dark, 2 = light.
     *
     * @param holder deferred holder of the item (e.g. {@code ClayiumItems.IMPURE_SILICON_INGOT})
     * @param base   color for layer 0 (base texture)
     * @param dark   color for layer 1 (dark texture)
     * @param light  color for layer 2 (light texture)
     */
    public static void register(DeferredHolder<Item, ?> holder, int base, int dark, int light) {
        ENTRIES.put(holder, new int[] { base, dark, light });
    }

    /** Returns whether the given item holder is registered as a colored ingot. */
    public static boolean isColoredIngot(DeferredHolder<Item, ?> holder) {
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
        // Colors from ClayiumUnofficial CMaterials (impureSilicon / silicon)
        register(ClayiumItems.IMPURE_SILICON_INGOT, 0xFF978F98, 0xFF533764, 0xFFA9A5A5);
        register(ClayiumItems.SILICON_INGOT, 0xFF281C28, 0xFF191919, 0xFFFFFFFF);
    }
}
