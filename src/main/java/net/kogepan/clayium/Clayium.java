package net.kogepan.clayium;

import net.kogepan.clayium.api.configuration.ConfigurationToolAction;
import net.kogepan.clayium.api.configuration.IConfigurationTool;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.datagen.ClayiumDataGenerators;
import net.kogepan.clayium.items.filter.FilterItemHelper;
import net.kogepan.clayium.items.filter.ItemFilterBase;
import net.kogepan.clayium.items.tools.ClaySteelToolMining;
import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.display.ClayiumRecipeBookCategories;
import net.kogepan.clayium.recipes.display.ClayiumRecipeDisplays;
import net.kogepan.clayium.recipes.display.ClayiumSlotDisplays;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumCreativeModeTabs;
import net.kogepan.clayium.registries.ClayiumDataComponents;
import net.kogepan.clayium.registries.ClayiumDataMaps;
import net.kogepan.clayium.registries.ClayiumFeatures;
import net.kogepan.clayium.registries.ClayiumFilterTypes;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.registries.ClayiumMenuTypes;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mod(Clayium.MODID)
public final class Clayium {

    public static final String MODID = "clayium";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final IConfigurationTool INSERTION_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.INSERTION,
            ConfigurationToolAction.INSERTION,
            false);
    private static final IConfigurationTool EXTRACTION_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.EXTRACTION,
            ConfigurationToolAction.EXTRACTION,
            false);
    private static final IConfigurationTool PIPING_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.PIPING,
            ConfigurationToolAction.PIPING,
            true);
    private static final IConfigurationTool FILTER_REMOVER_TOOL = new BuiltInConfigurationTool(
            ConfigurationToolAction.FILTER_REMOVER,
            ConfigurationToolAction.FILTER_REMOVER,
            false);

    public Clayium(IEventBus modEventBus, ModContainer modContainer) {
        ClayiumBlocks.BLOCKS.register(modEventBus);
        ClayiumItems.ITEMS.register(modEventBus);
        ClayiumCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ClayiumBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ClayiumFilterTypes.FILTER_TYPES.register(modEventBus);
        ClayiumDataComponents.DATA_COMPONENTS.register(modEventBus);
        ClayiumRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ClayiumRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ClayiumRecipeBookCategories.RECIPE_BOOK_CATEGORIES.register(modEventBus);
        ClayiumRecipeDisplays.RECIPE_DISPLAYS.register(modEventBus);
        ClayiumSlotDisplays.SLOT_DISPLAYS.register(modEventBus);
        ClayiumFeatures.FEATURES.register(modEventBus);
        ClayiumMenuTypes.MENU_TYPES.register(modEventBus);

        modEventBus.addListener(ClayiumDataGenerators::gatherData);
        modEventBus.addListener(Clayium::registerDataMapTypes);
        modEventBus.addListener(Clayium::registerCapabilities);

        NeoForge.EVENT_BUS.addListener(ClaySteelToolMining::onLeftClickBlock);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ClaySteelToolMining::onBreakSpeed);
        NeoForge.EVENT_BUS.addListener(ClaySteelToolMining::onServerTick);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    }

    private static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ClayiumDataMaps.CLAY_ENERGY);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                ClayiumCapabilities.CONFIGURATION_TOOL,
                (stack, context) -> INSERTION_TOOL,
                ClayiumItems.CLAY_ROLLING_PIN.get());
        event.registerItem(
                ClayiumCapabilities.CONFIGURATION_TOOL,
                (stack, context) -> EXTRACTION_TOOL,
                ClayiumItems.CLAY_SLICER.get());
        event.registerItem(
                ClayiumCapabilities.CONFIGURATION_TOOL,
                (stack, context) -> PIPING_TOOL,
                ClayiumItems.CLAY_SPATULA.get());
        event.registerItem(
                ClayiumCapabilities.CONFIGURATION_TOOL,
                (stack, context) -> FILTER_REMOVER_TOOL,
                ClayiumItems.RAW_CLAY_ROLLING_PIN.get(),
                ClayiumItems.RAW_CLAY_SPATULA.get(),
                ClayiumItems.RAW_CLAY_SLICER.get());

        event.registerBlockEntity(
                ClayiumCapabilities.MACHINE_CONFIGURABLE,
                ClayiumBlockEntityTypes.BENDING_MACHINE_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity);
        event.registerBlockEntity(
                ClayiumCapabilities.MACHINE_CONFIGURABLE,
                ClayiumBlockEntityTypes.CLAY_BUFFER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity);

        event.registerBlockEntity(
                ClayiumCapabilities.ITEM_FILTER_APPLICATABLE,
                ClayiumBlockEntityTypes.BENDING_MACHINE_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity);
        event.registerBlockEntity(
                ClayiumCapabilities.ITEM_FILTER_APPLICATABLE,
                ClayiumBlockEntityTypes.CLAY_BUFFER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity);

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ClayiumBlockEntityTypes.BENDING_MACHINE_BLOCK_ENTITY.get(),
                ClayContainerBlockEntity::getExposedItemHandler);
        event.registerBlockEntity(
                ClayiumCapabilities.CLAY_ENERGY,
                ClayiumBlockEntityTypes.BENDING_MACHINE_BLOCK_ENTITY.get(),
                ClayContainerBlockEntity::getExposedClayEnergyHandler);
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ClayiumBlockEntityTypes.CLAY_BUFFER_BLOCK_ENTITY.get(),
                ClayContainerBlockEntity::getExposedItemHandler);

        event.registerItem(
                ClayiumCapabilities.ITEM_FILTER_DATA,
                (stack, context) -> {
                    var copied = FilterItemHelper.getCopiedFilterData(stack);
                    if (copied != null) {
                        return copied;
                    }
                    return stack.getItem() instanceof ItemFilterBase filterItem ? filterItem.createFilterData(stack) :
                            null;
                },
                ClayiumItems.SIMPLE_ITEM_FILTER.get(),
                ClayiumItems.FAZY_ITEM_FILTER.get(),
                ClayiumItems.UNLOCALIZED_NAME_ITEM_FILTER.get());
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    private record BuiltInConfigurationTool(
                                            ConfigurationToolAction primaryAction,
                                            ConfigurationToolAction secondaryAction,
                                            boolean rendersMachineIOOverlay)
            implements IConfigurationTool {

        @Override
        public ConfigurationToolAction getAction(boolean secondaryUse) {
            return secondaryUse ? this.secondaryAction : this.primaryAction;
        }
    }
}
