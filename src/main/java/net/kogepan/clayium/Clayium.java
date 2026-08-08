package net.kogepan.clayium;

import net.kogepan.clayium.api.configuration.ConfigurationToolAction;
import net.kogepan.clayium.api.configuration.IConfigurationTool;
import net.kogepan.clayium.api.machine.replacement.DefaultMachineReplacementAdapter;
import net.kogepan.clayium.api.machine.replacement.MachineReplacementApi;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.ClayCraftingBoardBlockEntity;
import net.kogepan.clayium.blockentities.ClayInterfaceBlockEntity;
import net.kogepan.clayium.blockentities.LaserReflectorBlockEntity;
import net.kogepan.clayium.blockentities.machine.ChunkLoaderBlockEntity;
import net.kogepan.clayium.blockentities.machine.CreativeCESourceBlockEntity;
import net.kogepan.clayium.blockentities.trait.ItemFilterHolderTrait;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.IClayLaserAcceptor;
import net.kogepan.clayium.capability.IClayLaserSource;
import net.kogepan.clayium.capability.IExternalControl;
import net.kogepan.clayium.capability.IItemFilterApplicatable;
import net.kogepan.clayium.capability.ISynchronizedInterface;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.items.blockitem.StorageContainerBlockItem;
import net.kogepan.clayium.items.filter.FilterItemHelper;
import net.kogepan.clayium.items.filter.ItemFilterBase;
import net.kogepan.clayium.items.gadget.ClayGadgetHolderItem;
import net.kogepan.clayium.items.gadget.ClayGadgetItem;
import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumAttachments;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumDataComponents;
import net.kogepan.clayium.registries.ClayiumDataMaps;
import net.kogepan.clayium.registries.ClayiumFeatures;
import net.kogepan.clayium.registries.ClayiumFilterTypes;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.registries.ClayiumMenuTypes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
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
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
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

    private static final IConfigurationTool INSERTION_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.INSERTION, ConfigurationToolAction.INSERTION, false);
    private static final IConfigurationTool EXTRACTION_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.EXTRACTION, ConfigurationToolAction.EXTRACTION, false);
    private static final IConfigurationTool PIPING_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.PIPING, ConfigurationToolAction.PIPING, true);
    private static final IConfigurationTool ROTATION_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.ROTATION, ConfigurationToolAction.ROTATION, false);
    private static final IConfigurationTool IO_CONFIGURATOR_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.INSERTION, ConfigurationToolAction.EXTRACTION, true);
    private static final IConfigurationTool PIPING_CONFIGURATOR_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.PIPING, ConfigurationToolAction.ROTATION, true);
    private static final IConfigurationTool FILTER_REMOVER_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.FILTER_REMOVER, ConfigurationToolAction.FILTER_REMOVER, false);

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
                            Item registeredItem = item.get();
                            output.accept(registeredItem);
                            if (registeredItem == ClayiumBlocks.STORAGE_CONTAINER.asItem()) {
                                output.accept(StorageContainerBlockItem.createWithCapacity(Integer.MAX_VALUE));
                            }
                        }
                    }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Clayium(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ClayiumItems.ITEMS.register(modEventBus);
        ClayiumBlocks.BLOCKS.register(modEventBus);
        ClayiumAttachments.register(modEventBus);
        ClayiumDataComponents.DATA_COMPONENTS.register(modEventBus);
        ClayiumBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ClayiumRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ClayiumRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ClayiumFeatures.FEATURES.register(modEventBus);
        ClayiumFilterTypes.FILTER_TYPES.register(modEventBus);
        ClayiumMenuTypes.MENUS.register(modEventBus);

        modEventBus.addListener(this::registerCapacilities);
        modEventBus.addListener(this::registerDataMapTypes);
        modEventBus.addListener(this::registerTicketControllers);

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
        event.enqueueWork(() -> {
            for (DeferredHolder<BlockEntityType<?>, ?> holder : ClayiumBlockEntityTypes.CLAY_CONTAINER_BLOCK_ENTITY_TYPES) {
                MachineReplacementApi.register(holder.get(), new DefaultMachineReplacementAdapter(holder.getId()));
            }
        });

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
        LOGGER.info("HELLO from server starting");
    }

    public void registerCapacilities(RegisterCapabilitiesEvent event) {
        for (DeferredHolder<BlockEntityType<?>, ?> type : ClayiumBlockEntityTypes.CLAY_CONTAINER_BLOCK_ENTITY_TYPES) {
            event.registerBlockEntity(
                    ClayiumCapabilities.MACHINE_CONFIGURABLE,
                    type.get(),
                    (blockEntity, context) -> (ClayContainerBlockEntity) blockEntity);

            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    type.get(),
                    (blockEntity, side) -> ((ClayContainerBlockEntity) blockEntity).getExposedItemHandler(side));

            event.registerBlockEntity(
                    ClayiumCapabilities.CLAY_ENERGY_HOLDER,
                    type.get(),
                    (blockEntity, side) -> ((ClayContainerBlockEntity) blockEntity).getExposedClayEnergyHolder(side));

            event.registerBlockEntity(
                    ClayiumCapabilities.ITEM_FILTER_APPLICATABLE,
                    type.get(),
                    (blockEntity, side) -> {
                        ClayContainerBlockEntity container = (ClayContainerBlockEntity) blockEntity;
                        if (container instanceof ClayInterfaceBlockEntity clayInterface &&
                                !clayInterface.hasValidTarget()) {
                            return null;
                        }
                        var trait = container.getTrait(ItemFilterHolderTrait.TRAIT_ID);
                        return trait instanceof IItemFilterApplicatable a ? a : null;
                    });
        }

        event.registerBlockEntity(
                ClayiumCapabilities.MACHINE_CONFIGURABLE,
                ClayiumBlockEntityTypes.LASER_REFLECTOR_BLOCK_ENTITY.get(),
                (blockEntity, context) -> (LaserReflectorBlockEntity) blockEntity);

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ClayiumBlockEntityTypes.CREATIVE_CE_SOURCE_BLOCK_ENTITY.get(),
                (blockEntity, side) -> ((CreativeCESourceBlockEntity) blockEntity).getItemHandler(side));

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ClayiumBlockEntityTypes.CLAY_CRAFTING_BOARD_BLOCK_ENTITY.get(),
                (blockEntity, side) -> ((ClayCraftingBoardBlockEntity) blockEntity).getInventory());

        for (DeferredHolder<BlockEntityType<?>, ?> type : ClayiumBlockEntityTypes.BLOCK_ENTITY_TYPES.getEntries()) {
            event.registerBlockEntity(
                    ClayiumCapabilities.SYNCHRONIZED_INTERFACE,
                    type.get(),
                    (blockEntity, side) -> blockEntity instanceof ISynchronizedInterface sync ? sync : null);

            event.registerBlockEntity(
                    ClayiumCapabilities.CLAY_LASER_SOURCE,
                    type.get(),
                    (blockEntity, side) -> blockEntity instanceof IClayLaserSource source ? source : null);

            event.registerBlockEntity(
                    ClayiumCapabilities.CLAY_LASER_ACCEPTOR,
                    type.get(),
                    (blockEntity, side) -> blockEntity instanceof IClayLaserAcceptor acceptor ? acceptor : null);

            event.registerBlockEntity(
                    ClayiumCapabilities.EXTERNAL_CONTROL,
                    type.get(),
                    (blockEntity, side) -> blockEntity instanceof IExternalControl control ? control : null);
        }

        // Serializable filter data capability for item-side persistence.
        event.registerItem(ClayiumCapabilities.ITEM_FILTER_DATA, (stack, context) -> {
            ItemFilterData copied = FilterItemHelper.getCopiedFilterData(stack);
            if (copied != null) {
                return copied;
            }
            Item item = stack.getItem();
            return item instanceof ItemFilterBase filterItem ? filterItem.createFilterData(stack) : null;
        }, ClayiumItems.SIMPLE_ITEM_FILTER.get(), ClayiumItems.FAZY_ITEM_FILTER.get(),
                ClayiumItems.UNLOCALIZED_NAME_ITEM_FILTER.get());

        event.registerItem(Capabilities.ItemHandler.ITEM,
                (stack, context) -> ClayGadgetHolderItem.createHandler(stack),
                ClayiumItems.CLAY_GADGET_HOLDER.get());

        event.registerItem(ClayiumCapabilities.CLAY_GADGET,
                (stack, context) -> stack.getItem() instanceof ClayGadgetItem gadgetItem ? gadgetItem.getGadget() :
                        null,
                ClayiumItems.GADGET_OVERCLOCKER_MK1.get(), ClayiumItems.GADGET_OVERCLOCKER_MK2.get(),
                ClayiumItems.GADGET_OVERCLOCKER_MK3.get(), ClayiumItems.GADGET_OVERCLOCKER_MK4.get(),
                ClayiumItems.GADGET_FLIGHT_MK1.get(), ClayiumItems.GADGET_FLIGHT_MK2.get(),
                ClayiumItems.GADGET_FLIGHT_MK3.get(), ClayiumItems.GADGET_HEALTH_MK1.get(),
                ClayiumItems.GADGET_HEALTH_MK2.get(), ClayiumItems.GADGET_HEALTH_MK3.get(),
                ClayiumItems.GADGET_AUTO_EAT_ECONOMICAL.get(), ClayiumItems.GADGET_AUTO_EAT.get(),
                ClayiumItems.GADGET_REPEATEDLY_ATTACK.get(), ClayiumItems.GADGET_LONG_ARM_MK1.get(),
                ClayiumItems.GADGET_LONG_ARM_MK2.get(), ClayiumItems.GADGET_LONG_ARM_MK3.get());

        event.registerItem(ClayiumCapabilities.CONFIGURATION_TOOL, (stack, context) -> INSERTION_TOOL,
                ClayiumItems.CLAY_ROLLING_PIN.get());
        event.registerItem(ClayiumCapabilities.CONFIGURATION_TOOL, (stack, context) -> EXTRACTION_TOOL,
                ClayiumItems.CLAY_SLICER.get());
        event.registerItem(ClayiumCapabilities.CONFIGURATION_TOOL, (stack, context) -> PIPING_TOOL,
                ClayiumItems.CLAY_SPATULA.get());
        event.registerItem(ClayiumCapabilities.CONFIGURATION_TOOL, (stack, context) -> ROTATION_TOOL,
                ClayiumItems.CLAY_WRENCH.get());
        event.registerItem(ClayiumCapabilities.CONFIGURATION_TOOL, (stack, context) -> IO_CONFIGURATOR_TOOL,
                ClayiumItems.CLAY_IO_CONFIGURATOR.get());
        event.registerItem(ClayiumCapabilities.CONFIGURATION_TOOL, (stack, context) -> PIPING_CONFIGURATOR_TOOL,
                ClayiumItems.CLAY_PIPING_CONFIGURATOR.get());
        event.registerItem(ClayiumCapabilities.CONFIGURATION_TOOL, (stack, context) -> FILTER_REMOVER_TOOL,
                ClayiumItems.RAW_CLAY_ROLLING_PIN.get(), ClayiumItems.RAW_CLAY_SLICER.get(),
                ClayiumItems.RAW_CLAY_SPATULA.get());
    }

    public void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ClayiumDataMaps.CLAY_ENERGY);
    }

    public void registerTicketControllers(RegisterTicketControllersEvent event) {
        event.register(ChunkLoaderBlockEntity.TICKET_CONTROLLER);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private record BuiltInConfigurationTool(ConfigurationToolAction primaryAction,
                                            ConfigurationToolAction secondaryAction,
                                            boolean rendersMachineIOOverlay)
            implements IConfigurationTool {

        @Override
        public ConfigurationToolAction getAction(boolean secondaryUse) {
            return secondaryUse ? this.secondaryAction : this.primaryAction;
        }
    }
}
