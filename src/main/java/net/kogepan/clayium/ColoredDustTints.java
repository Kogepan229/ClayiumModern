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

        // Material dusts (Original: CMaterials IMPURE_SILICON 151,143,152 / 83,55,100 / 169,165,165)
        int[] impureSilicon = { 0xFF978F98, 0xFF533764, 0xFFA9A5A5 };
        // Silicon (Original: 40,28,40 and 255,255,255 for layer 2; dark from plate style)
        int[] silicon = { 0xFF281C28, 0xFF060406, 0xFFFFFFFF };
        int[] silicone = { 0xFFD2D2D2, 0xFFB4B4B4, 0xFFF0F0F0 };
        int[] aluminium = { 0xFFBEC8CA, 0xFF1F2121, 0xFFFFFFFF };
        int[] impureAluminium = { 0xFFBEC8CA, 0xFF78783C, 0xFFDCDCDC };
        int[] claySteel = { 0xFF8890AD, 0xFF16181C, 0xFFFFFFFF };
        int[] clayium = { 0xFF5AF0D2, 0xFF3F4855, 0xFFFFCDC8 };
        int[] ultimateAlloy = { 0xFF55CD55, 0xFF0E220E, 0xFFF5A0FF };
        int[] az91d = { 0xFF828C87, 0xFF0A280A, 0xFFFFFFFF };
        int[] zk60a = { 0xFF4B5550, 0xFF0A280A, 0xFFFFFFFF };
        int[] manganese = { 0xFFBEF0F0, 0xFF1F2828, 0xFFFFFFFF };
        int[] lithium = { 0xFFD2D296, 0xFF787878, 0xFFFFFFFF };
        int[] zirconium = { 0xFFBEAA7A, 0xFF787878, 0xFFFFFFFF };

        register(ClayiumItems.MANGANESE_DUST, manganese[0], manganese[1], manganese[2]);
        register(ClayiumItems.LITHIUM_DUST, lithium[0], lithium[1], lithium[2]);
        register(ClayiumItems.ZIRCONIUM_DUST, zirconium[0], zirconium[1], zirconium[2]);
        register(ClayiumItems.IMPURE_SILICON_DUST, impureSilicon[0], impureSilicon[1], impureSilicon[2]);
        register(ClayiumItems.SILICON_DUST, silicon[0], silicon[1], silicon[2]);
        register(ClayiumItems.SILICONE_DUST, silicone[0], silicone[1], silicone[2]);
        register(ClayiumItems.ALUMINIUM_DUST, aluminium[0], aluminium[1], aluminium[2]);
        register(ClayiumItems.IMPURE_ALUMINIUM_DUST, impureAluminium[0], impureAluminium[1], impureAluminium[2]);
        register(ClayiumItems.CLAY_STEEL_DUST, claySteel[0], claySteel[1], claySteel[2]);
        register(ClayiumItems.CLAYIUM_DUST, clayium[0], clayium[1], clayium[2]);
        register(ClayiumItems.ULTIMATE_ALLOY_DUST, ultimateAlloy[0], ultimateAlloy[1], ultimateAlloy[2]);
        register(ClayiumItems.AZ91D_ALLOY_DUST, az91d[0], az91d[1], az91d[2]);
        register(ClayiumItems.ZK60A_ALLOY_DUST, zk60a[0], zk60a[1], zk60a[2]);

        // Chemical reactor dusts (Original: ImpureRedstone 151,70,70; ImpureGlowstone 151,151,70)
        int[] impureRedstone = { 0xFF974646, 0xFF3D1E1E, 0xFFCC6666 };
        int[] impureGlowstone = { 0xFF979746, 0xFF3D3D1E, 0xFFCCCC66 };
        register(ClayiumItems.IMPURE_REDSTONE_DUST, impureRedstone[0], impureRedstone[1], impureRedstone[2]);
        register(ClayiumItems.IMPURE_GLOWSTONE_DUST, impureGlowstone[0], impureGlowstone[1], impureGlowstone[2]);
    }
}
