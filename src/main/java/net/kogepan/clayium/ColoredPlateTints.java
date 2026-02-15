package net.kogepan.clayium;

import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry for items that use the 3-layer plate model (base, dark, light) with
 * per-layer tint colors. Used by client ItemColors and by datagen for item
 * models. Supports both normal plates (plate_*) and large plates (largeplate_*).
 */
public final class ColoredPlateTints {

    /** Number of texture layers (and tint indices) for colored plates. */
    public static final int LAYER_COUNT = 3;

    private static final Map<DeferredHolder<Item, ?>, Entry> ENTRIES = new LinkedHashMap<>();

    private ColoredPlateTints() {}

    /**
     * Registers an item to use the 3-layer plate model with the given colors.
     * Colors are ARGB (e.g. 0xFFRRGGBB). Layer 0 = base, 1 = dark, 2 = light.
     *
     * @param holder     deferred holder of the item
     * @param base       color for layer 0 (base texture)
     * @param dark       color for layer 1 (dark texture)
     * @param light      color for layer 2 (light texture)
     * @param largePlate if true, use largeplate_* textures; otherwise plate_*
     */
    public static void register(DeferredHolder<Item, ?> holder, int base, int dark, int light, boolean largePlate) {
        ENTRIES.put(holder, new Entry(base, dark, light, largePlate));
    }

    /** Returns whether the given item holder is registered as a colored plate. */
    public static boolean isColoredPlate(DeferredHolder<Item, ?> holder) {
        return ENTRIES.containsKey(holder);
    }

    /**
     * Returns the tint color for the given holder and layer index, or 0xFFFFFFFF
     * if not registered or index out of range.
     */
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

    /** Returns true if the plate uses largeplate_* textures. */
    public static boolean isLargePlate(DeferredHolder<Item, ?> holder) {
        Entry e = ENTRIES.get(holder);
        return e != null && e.largePlate;
    }

    /** Returns an unmodifiable view of all registered item holders. */
    public static Collection<DeferredHolder<Item, ?>> getRegisteredHolders() {
        return Collections.unmodifiableCollection(ENTRIES.keySet());
    }

    private record Entry(int base, int dark, int light, boolean largePlate) {}

    static {
        int[] silicone = { 0xFFD2D2D2, 0xFFB4B4B4, 0xFFF0F0F0 };
        int[] silicon = { 0xFF281C28, 0xFF060406, 0xFFFFFFFF };
        int[] aluminium = { 0xFFBEC8CA, 0xFF1F2121, 0xFFFFFFFF };
        int[] impureAluminium = { 0xFFBEC8CA, 0xFF78783C, 0xFFDCDCDC };
        int[] claySteel = { 0xFF8890AD, 0xFF16181C, 0xFFFFFFFF };
        int[] clayium = { 0xFF5AF0D2, 0xFF3F4855, 0xFFFFCDC8 };
        int[] ultimateAlloy = { 0xFF55CD55, 0xFF0E220E, 0xFFF5A0FF };
        int[] antimatter = { 0xFF0000EB, 0xFF000000, 0xFFFFFFFF };
        int[] pureAntimatter = { 0xFFFF32FF, 0xFF000000, 0xFFFFFFFF };
        int[] octupleClay = { 0xFFFFFF00, 0xFF8C8C8C, 0xFFFFFFFF };
        int[] octuplePureAntimatter = { 0xFF960000, 0xFFC8C800, 0xFFFFFFFF };
        int[] az91d = { 0xFF828C87, 0xFF0A280A, 0xFFFFFFFF };
        int[] zk60a = { 0xFF4B5550, 0xFF0A280A, 0xFFFFFFFF };

        register(ClayiumItems.SILICONE_PLATE, silicone[0], silicone[1], silicone[2], false);
        register(ClayiumItems.LARGE_SILICONE_PLATE, silicone[0], silicone[1], silicone[2], true);
        register(ClayiumItems.SILICON_PLATE, silicon[0], silicon[1], silicon[2], false);
        register(ClayiumItems.LARGE_SILICON_PLATE, silicon[0], silicon[1], silicon[2], true);
        register(ClayiumItems.ALUMINIUM_PLATE, aluminium[0], aluminium[1], aluminium[2], false);
        register(ClayiumItems.LARGE_ALUMINIUM_PLATE, aluminium[0], aluminium[1], aluminium[2], true);
        register(ClayiumItems.IMPURE_ALUMINIUM_PLATE, impureAluminium[0], impureAluminium[1], impureAluminium[2],
                false);
        register(ClayiumItems.LARGE_IMPURE_ALUMINIUM_PLATE, impureAluminium[0], impureAluminium[1], impureAluminium[2],
                true);
        register(ClayiumItems.CLAY_STEEL_PLATE, claySteel[0], claySteel[1], claySteel[2], false);
        register(ClayiumItems.LARGE_CLAY_STEEL_PLATE, claySteel[0], claySteel[1], claySteel[2], true);
        register(ClayiumItems.CLAYIUM_PLATE, clayium[0], clayium[1], clayium[2], false);
        register(ClayiumItems.LARGE_CLAYIUM_PLATE, clayium[0], clayium[1], clayium[2], true);
        register(ClayiumItems.ULTIMATE_ALLOY_PLATE, ultimateAlloy[0], ultimateAlloy[1], ultimateAlloy[2], false);
        register(ClayiumItems.LARGE_ULTIMATE_ALLOY_PLATE, ultimateAlloy[0], ultimateAlloy[1], ultimateAlloy[2], true);
        register(ClayiumItems.ANTIMATTER_PLATE, antimatter[0], antimatter[1], antimatter[2], false);
        register(ClayiumItems.LARGE_ANTIMATTER_PLATE, antimatter[0], antimatter[1], antimatter[2], true);
        register(ClayiumItems.PURE_ANTIMATTER_PLATE, pureAntimatter[0], pureAntimatter[1], pureAntimatter[2], false);
        register(ClayiumItems.LARGE_PURE_ANTIMATTER_PLATE, pureAntimatter[0], pureAntimatter[1], pureAntimatter[2],
                true);
        register(ClayiumItems.OCTUPLE_ENERGETIC_CLAY_PLATE, octupleClay[0], octupleClay[1], octupleClay[2], false);
        register(ClayiumItems.LARGE_OCTUPLE_ENERGETIC_CLAY_PLATE, octupleClay[0], octupleClay[1], octupleClay[2], true);
        register(ClayiumItems.OCTUPLE_PURE_ANTIMATTER_PLATE, octuplePureAntimatter[0], octuplePureAntimatter[1],
                octuplePureAntimatter[2], false);
        register(ClayiumItems.LARGE_OCTUPLE_PURE_ANTIMATTER_PLATE, octuplePureAntimatter[0], octuplePureAntimatter[1],
                octuplePureAntimatter[2], true);
        register(ClayiumItems.AZ91D_ALLOY_PLATE, az91d[0], az91d[1], az91d[2], false);
        register(ClayiumItems.LARGE_AZ91D_ALLOY_PLATE, az91d[0], az91d[1], az91d[2], true);
        register(ClayiumItems.ZK60A_ALLOY_PLATE, zk60a[0], zk60a[1], zk60a[2], false);
        register(ClayiumItems.LARGE_ZK60A_ALLOY_PLATE, zk60a[0], zk60a[1], zk60a[2], true);
    }
}
