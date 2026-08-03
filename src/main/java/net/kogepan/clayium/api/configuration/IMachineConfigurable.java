package net.kogepan.clayium.api.configuration;

import net.minecraft.world.item.context.UseOnContext;

/**
 * Block capability exposed by targets that accept configuration-tool operations.
 *
 * <p>
 * Add-on block entities can expose themselves without extending a Clayium class:
 *
 * <pre>{@code
 * event.registerBlockEntity(
 *         ClayiumCapabilities.MACHINE_CONFIGURABLE,
 *         MY_BLOCK_ENTITY_TYPE.get(),
 *         (blockEntity, context) -> blockEntity);
 * }</pre>
 *
 * <p>
 * Plain blocks can expose a provider with {@code RegisterCapabilitiesEvent#registerBlock} instead.
 */
public interface IMachineConfigurable {

    /** This method is queried on both logical sides and must not mutate state. */
    boolean canConfigure(ConfigurationToolAction action, UseOnContext context);

    /** This method is invoked on the logical server only. */
    void configure(ConfigurationToolAction action, UseOnContext context);
}
