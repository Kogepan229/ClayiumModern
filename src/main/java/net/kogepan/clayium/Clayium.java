package net.kogepan.clayium;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.machine.CreativeCESourceBlockEntity;
import net.kogepan.clayium.blockentities.trait.ClayEnergyHolder;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.IClayEnergyHolder;
import net.kogepan.clayium.capability.IClayLaserAcceptor;
import net.kogepan.clayium.capability.IClayLaserSource;
import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumDataMaps;
import net.kogepan.clayium.registries.ClayiumFeatures;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mod(Clayium.MODID)
public class Clayium {

    public static final String MODID = "clayium";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "clayium" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS
            .register("clayium_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.clayium"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(Items.CLAY_BALL::getDefaultInstance)
                    .displayItems((parameters, output) -> {
                        for (var item : ClayiumItems.ITEMS.getEntries()) {
                            output.accept(item.get());
                        }
                    }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Clayium(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ClayiumItems.ITEMS.register(modEventBus);
        ClayiumBlocks.BLOCKS.register(modEventBus);
        ClayiumBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ClayiumRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ClayiumRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ClayiumFeatures.FEATURES.register(modEventBus);

        modEventBus.addListener(this::registerCapacilities);
        modEventBus.addListener(this::registerDataMapTypes);

        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Clayium) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like
        // onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public void registerCapacilities(RegisterCapabilitiesEvent event) {
        for (DeferredHolder<BlockEntityType<?>, ?> type : ClayiumBlockEntityTypes.CLAY_CONTAINER_BLOCK_ENTITY_TYPES) {
            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    type.get(),
                    (blockEntity, side) -> ((ClayContainerBlockEntity) blockEntity).getExposedItemHandler(side));

            event.registerBlockEntity(
                    ClayiumCapabilities.CLAY_ENERGY_HOLDER,
                    type.get(),
                    (blockEntity, side) -> {
                        ClayContainerBlockEntity container = (ClayContainerBlockEntity) blockEntity;
                        var trait = container.getTrait(ClayEnergyHolder.TRAIT_ID);
                        return trait instanceof IClayEnergyHolder ? (IClayEnergyHolder) trait : null;
                    });
        }

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ClayiumBlockEntityTypes.CREATIVE_CE_SOURCE_BLOCK_ENTITY.get(),
                (blockEntity, side) -> ((CreativeCESourceBlockEntity) blockEntity).getItemHandler(side));

        for (DeferredHolder<BlockEntityType<?>, ?> type : ClayiumBlockEntityTypes.BLOCK_ENTITY_TYPES.getEntries()) {
            event.registerBlockEntity(
                    ClayiumCapabilities.CLAY_LASER_SOURCE,
                    type.get(),
                    (blockEntity, side) -> blockEntity instanceof IClayLaserSource source ? source : null);

            event.registerBlockEntity(
                    ClayiumCapabilities.CLAY_LASER_ACCEPTOR,
                    type.get(),
                    (blockEntity, side) -> blockEntity instanceof IClayLaserAcceptor acceptor ? acceptor : null);
        }
    }

    public void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ClayiumDataMaps.CLAY_ENERGY);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
