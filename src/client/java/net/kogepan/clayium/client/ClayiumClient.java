package net.kogepan.clayium.client;

import net.kogepan.clayium.Clayium;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Clayium.MODID, dist = Dist.CLIENT)
public final class ClayiumClient {

    public ClayiumClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(ClayiumClient::onClientSetup);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        Clayium.LOGGER.debug("Clayium client setup complete");
    }
}
