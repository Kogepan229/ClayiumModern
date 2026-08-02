package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE,
            Clayium.MODID);

    private ClayiumFeatures() {}
}
