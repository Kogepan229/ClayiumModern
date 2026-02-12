package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.worldgen.ClayOreVeinConfiguration;
import net.kogepan.clayium.worldgen.ClayOreVeinFeature;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registration for custom world generation features used by Clayium.
 */
public class ClayiumFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE,
            Clayium.MODID);

    public static final DeferredHolder<Feature<?>, Feature<ClayOreVeinConfiguration>> CLAY_ORE_VEIN = FEATURES
            .register("clay_ore_vein", ClayOreVeinFeature::new);
}
