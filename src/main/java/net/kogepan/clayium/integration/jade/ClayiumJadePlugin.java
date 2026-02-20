package net.kogepan.clayium.integration.jade;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.LaserReflectorBlockEntity;
import net.kogepan.clayium.blockentities.machine.ClayLaserBlockEntity;

import net.minecraft.world.level.block.Block;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(Clayium.MODID)
public class ClayiumJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ClayLaserSourceComponentProvider.INSTANCE, ClayLaserBlockEntity.class);
        registration.registerBlockDataProvider(ClayLaserSourceComponentProvider.INSTANCE,
                LaserReflectorBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(ClayLaserSourceComponentProvider.INSTANCE, Block.class);
    }
}
