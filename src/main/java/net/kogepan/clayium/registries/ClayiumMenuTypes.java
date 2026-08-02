package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU,
            Clayium.MODID);

    private ClayiumMenuTypes() {}
}
