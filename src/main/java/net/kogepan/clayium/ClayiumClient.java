package net.kogepan.clayium;

import net.kogepan.clayium.client.model.ModelTextures;
import net.kogepan.clayium.client.model.PipeOverlayQuads;
import net.kogepan.clayium.client.model.block.ClayContainerModelLoader;
import net.kogepan.clayium.client.renderer.PipedMachineIoRenderer;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Clayium.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with
// @SubscribeEvent
@EventBusSubscriber(modid = Clayium.MODID, value = Dist.CLIENT)
public class ClayiumClient {

    public ClayiumClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Clayium.LOGGER.info("HELLO FROM CLIENT SETUP");
        Clayium.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ClayContainerModelLoader.ID, ClayContainerModelLoader.INSTANCE);
    }

    @SuppressWarnings({ "deprecation" })
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAtlasStitched(TextureAtlasStitchedEvent event) {
        TextureAtlas atlas = event.getAtlas();
        if (atlas.location() == TextureAtlas.LOCATION_BLOCKS) {
            ModelTextures.initSprites(atlas);
            PipeOverlayQuads.initialize(atlas);
        }
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ClayiumBlockEntityTypes.TEST_CLAY_CONTAINER_BLOCK_ENTITY.get(),
                PipedMachineIoRenderer::new);
    }
}
