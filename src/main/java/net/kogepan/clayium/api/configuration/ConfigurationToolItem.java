package net.kogepan.clayium.api.configuration;

import net.kogepan.clayium.capability.ClayiumCapabilities;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Base item for configuration tools.
 *
 * <p>
 * Add-on tools extending this class must also expose {@link IConfigurationTool} through {@link
 * ClayiumCapabilities#CONFIGURATION_TOOL} during {@link RegisterCapabilitiesEvent}:
 *
 * <pre>{@code
 * event.registerItem(
 *         ClayiumCapabilities.CONFIGURATION_TOOL,
 *         (stack, context) -> MY_TOOL_BEHAVIOR,
 *         MY_TOOL_ITEM);
 * }</pre>
 *
 * <p>
 * Items that cannot extend this class can call {@link ConfigurationToolUseHelper#useConfigurationTool} from their own
 * {@code onItemUseFirst} implementation.
 */
public class ConfigurationToolItem extends Item {

    public ConfigurationToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return ConfigurationToolUseHelper.useConfigurationTool(stack, context);
    }
}
