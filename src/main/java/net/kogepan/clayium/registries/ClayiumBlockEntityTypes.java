package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(Registries.BLOCK_ENTITY_TYPE, Clayium.MODID);

    private ClayiumBlockEntityTypes() {}
}
