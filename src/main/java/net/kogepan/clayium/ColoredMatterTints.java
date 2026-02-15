package net.kogepan.clayium;

import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry for items that use the 3-layer matter/gem model (matter_*, matter2_*, etc.)
 * with per-layer tint colors. Used by client ItemColors and by datagen for item models.
 * Texture prefix determines which set of textures is used (e.g. "matter", "matter2", "matter5").
 */
public final class ColoredMatterTints {

    /** Number of texture layers (and tint indices) for colored matter. */
    public static final int LAYER_COUNT = 3;

    private static final Map<DeferredHolder<Item, ?>, Entry> ENTRIES = new LinkedHashMap<>();

    private ColoredMatterTints() {}

    /**
     * Registers an item to use the 3-layer matter model with the given colors and texture prefix.
     * Colors are ARGB. Layer 0 = base, 1 = dark, 2 = light.
     * Texture prefix is used as {@code prefix_base}, {@code prefix_dark}, {@code prefix_light}.
     */
    public static void register(DeferredHolder<Item, ?> holder, int base, int dark, int light, String texturePrefix) {
        ENTRIES.put(holder, new Entry(base, dark, light, texturePrefix));
    }

    /** Returns whether the given item holder is registered as colored matter. */
    public static boolean isColoredMatter(DeferredHolder<Item, ?> holder) {
        return ENTRIES.containsKey(holder);
    }

    /** Returns the tint color for the given holder and layer index, or 0xFFFFFFFF if not registered. */
    public static int getColor(DeferredHolder<Item, ?> holder, int tintIndex) {
        Entry e = ENTRIES.get(holder);
        if (e == null || tintIndex < 0 || tintIndex >= LAYER_COUNT) {
            return 0xFFFFFFFF;
        }
        return switch (tintIndex) {
            case 0 -> e.base;
            case 1 -> e.dark;
            case 2 -> e.light;
            default -> 0xFFFFFFFF;
        };
    }

    /** Returns the texture prefix for the given holder (e.g. "matter", "matter2"), or null. */
    public static String getTexturePrefix(DeferredHolder<Item, ?> holder) {
        Entry e = ENTRIES.get(holder);
        return e == null ? null : e.texturePrefix;
    }

    /** Returns an unmodifiable view of all registered item holders. */
    public static Collection<DeferredHolder<Item, ?>> getRegisteredHolders() {
        return Collections.unmodifiableCollection(ENTRIES.keySet());
    }

    private record Entry(int base, int dark, int light, String texturePrefix) {}

    static {
        // Antimatter / Pure Antimatter matter
        register(ClayiumItems.ANTIMATTER_MATTER, 0xFF0000EB, 0xFF000000, 0xFFFFFFFF, "matter");
        register(ClayiumItems.PURE_ANTIMATTER_MATTER, 0xFFFF32FF, 0xFF000000, 0xFFFFFFFF, "matter");

        // Compressed Pure Antimatter 1-8
        register(ClayiumItems.COMPRESSED_PURE_ANTIMATTER_1, 0xFFC423B5, 0xFF191900, 0xFFFFFFFF, "matter");
        register(ClayiumItems.COMPRESSED_PURE_ANTIMATTER_2, 0xFF8F1777, 0xFF323200, 0xFFFFFFFF, "matter2");
        register(ClayiumItems.COMPRESSED_PURE_ANTIMATTER_3, 0xFF5E0D45, 0xFF4B4B00, 0xFFFFFFFF, "matter2");
        register(ClayiumItems.COMPRESSED_PURE_ANTIMATTER_4, 0xFF32061F, 0xFF646400, 0xFFFFFFFF, "matter3");
        register(ClayiumItems.COMPRESSED_PURE_ANTIMATTER_5, 0xFF530829, 0xFF7D7D00, 0xFFFFFFFF, "matter3");
        register(ClayiumItems.COMPRESSED_PURE_ANTIMATTER_6, 0xFF6E0828, 0xFF969600, 0xFFFFFFFF, "matter4");
        register(ClayiumItems.COMPRESSED_PURE_ANTIMATTER_7, 0xFF84051A, 0xFFAFAF00, 0xFFFFFFFF, "matter4");
        register(ClayiumItems.COMPRESSED_PURE_ANTIMATTER_8, 0xFF960000, 0xFFC8C800, 0xFFFFFFFF, "matter5");
    }
}
