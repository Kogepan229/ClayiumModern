package net.kogepan.clayium;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumDataComponents;
import net.kogepan.clayium.registries.ClayiumDataMaps;
import net.kogepan.clayium.registries.ClayiumFeatures;
import net.kogepan.clayium.registries.ClayiumFilterTypes;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.registries.ClayiumMenuTypes;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mod(Clayium.MODID)
public final class Clayium {

    public static final String MODID = "clayium";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Clayium(IEventBus modEventBus, ModContainer modContainer) {
        ClayiumBlocks.BLOCKS.register(modEventBus);
        ClayiumItems.ITEMS.register(modEventBus);
        ClayiumBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ClayiumFilterTypes.FILTER_TYPES.register(modEventBus);
        ClayiumDataComponents.DATA_COMPONENTS.register(modEventBus);
        ClayiumRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ClayiumRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ClayiumFeatures.FEATURES.register(modEventBus);
        ClayiumMenuTypes.MENU_TYPES.register(modEventBus);

        modEventBus.addListener(Clayium::registerDataMapTypes);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ClayiumDataMaps.CLAY_ENERGY);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
