package net.kogepan.clayium;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mod(Clayium.MODID)
public final class Clayium {

    public static final String MODID = "clayium";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Clayium(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
