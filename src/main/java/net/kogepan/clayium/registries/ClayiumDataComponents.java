package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
            .createDataComponents(Registries.DATA_COMPONENT_TYPE, Clayium.MODID);

    private ClayiumDataComponents() {}
}
