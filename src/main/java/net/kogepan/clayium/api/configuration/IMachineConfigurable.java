package net.kogepan.clayium.api.configuration;

import net.minecraft.world.item.context.UseOnContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Block capability exposed by blocks that accept configuration-tool operations.
 *
 * <p>
 * A block entity can expose itself without extending a Clayium class:
 *
 * <pre>{@code
 * event.registerBlockEntity(
 *         ClayiumCapabilities.MACHINE_CONFIGURABLE,
 *         MY_BLOCK_ENTITY_TYPE.get(),
 *         (blockEntity, context) -> blockEntity);
 * }</pre>
 *
 * <p>
 * A block without a block entity can expose a provider directly:
 *
 * <pre>{@code
 * event.registerBlock(
 *         ClayiumCapabilities.MACHINE_CONFIGURABLE,
 *         (level, pos, state, blockEntity, context) -> MY_CONFIGURABLE,
 *         MY_BLOCK.get());
 * }</pre>
 */
public interface IMachineConfigurable {

    /**
     * Returns whether this target handles the requested operation.
     *
     * <p>
     * This method is called on both logical sides and must not mutate game state.
     */
    boolean canConfigure(@NotNull ConfigurationToolAction action, @NotNull UseOnContext context);

    /**
     * Applies the requested operation.
     *
     * <p>
     * This method is invoked by {@link ConfigurationToolUseHelper} on the logical server only.
     * Implementations must not depend on it being called on the client.
     */
    void configure(@NotNull ConfigurationToolAction action, @NotNull UseOnContext context);

    /**
     * Returns whether this target supports the standard six-side Memory Card format.
     *
     * <p>
     * This method is called on both logical sides and must not mutate game state.
     */
    default boolean supportsConfigurationMemory(@NotNull UseOnContext context) {
        return false;
    }

    /**
     * Captures settings in the standard Memory Card format.
     *
     * <p>
     * Callers invoke this method on the logical server only. Returning {@code null} rejects saving.
     */
    @Nullable
    default MachineIOConfiguration captureConfiguration(@NotNull UseOnContext context) {
        return null;
    }

    /**
     * Applies settings from the standard Memory Card format.
     *
     * <p>
     * Callers invoke this method on the logical server only. Returning {@code false} rejects incompatible data.
     */
    default boolean applyConfiguration(@NotNull UseOnContext context,
                                       @NotNull MachineIOConfiguration configuration) {
        return false;
    }
}
