package net.kogepan.clayium.api.gadget;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class ClayGadgetContext {

    private final Player player;
    private final Function<ItemStack, Boolean> stackWriter;
    private ItemStack stack;

    ClayGadgetContext(@NotNull Player player, @NotNull ItemStack stack,
                      @NotNull Function<ItemStack, Boolean> stackWriter) {
        this.player = player;
        this.stack = stack.copy();
        this.stackWriter = stackWriter;
    }

    /** Returns the player carrying the holder. */
    @NotNull
    public Player player() {
        return player;
    }

    /** Returns a detached copy of the installed gadget stack. */
    @NotNull
    public ItemStack stack() {
        return stack.copy();
    }

    /**
     * Replaces the installed stack and writes it back to the holder.
     *
     * @return whether the write was accepted; client-side writes are always rejected
     */
    public boolean replaceStack(@NotNull ItemStack replacement) {
        if (player.level().isClientSide()) {
            return false;
        }
        ItemStack normalized = replacement.copy();
        if (!stackWriter.apply(normalized)) {
            return false;
        }
        stack = normalized;
        return true;
    }

    /** Applies an immutable-style update and writes the result back to the holder. */
    public boolean updateStack(@NotNull UnaryOperator<ItemStack> updater) {
        ItemStack replacement = updater.apply(stack());
        return replacement != null && replaceStack(replacement);
    }
}
