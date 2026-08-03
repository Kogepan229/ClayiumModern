package net.kogepan.clayium.api.configuration;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import org.jetbrains.annotations.NotNull;

/**
 * Base item for configuration tools.
 *
 * <p>
 * Add-on items extending this class must expose {@code IConfigurationTool} through
 * {@code ClayiumCapabilities.CONFIGURATION_TOOL} during {@code RegisterCapabilitiesEvent}:
 *
 * <pre>{@code
 * event.registerItem(
 *         ClayiumCapabilities.CONFIGURATION_TOOL,
 *         (stack, context) -> MY_TOOL_BEHAVIOR,
 *         MY_TOOL_ITEM);
 * }</pre>
 *
 * <p>
 * Items that cannot extend this class can call {@link
 * ConfigurationToolUseHelper#useConfigurationTool(ItemStack, UseOnContext)} from their own {@code onItemUseFirst}
 * implementation.
 */
public class ConfigurationToolItem extends Item {

    public ConfigurationToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext context) {
        return ConfigurationToolUseHelper.useConfigurationTool(stack, context);
    }
}
