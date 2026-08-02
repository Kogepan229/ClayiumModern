package net.kogepan.clayium.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.Objects;
import java.util.function.BiConsumer;

/** Item storage that reports committed slot changes to its owner. */
public class ClayiumItemStorage extends ItemStacksResourceHandler {

    private static final BiConsumer<Integer, ItemStack> NO_OP_LISTENER = (index, previousContents) -> {};

    private final BiConsumer<Integer, ItemStack> contentsChangedListener;

    public ClayiumItemStorage(int size) {
        this(size, NO_OP_LISTENER);
    }

    public ClayiumItemStorage(int size, BiConsumer<Integer, ItemStack> contentsChangedListener) {
        super(size);
        this.contentsChangedListener = Objects.requireNonNull(contentsChangedListener);
    }

    public ClayiumItemStorage(NonNullList<ItemStack> stacks) {
        this(stacks, NO_OP_LISTENER);
    }

    public ClayiumItemStorage(
                              NonNullList<ItemStack> stacks,
                              BiConsumer<Integer, ItemStack> contentsChangedListener) {
        super(stacks);
        this.contentsChangedListener = Objects.requireNonNull(contentsChangedListener);
    }

    @Override
    protected final void onContentsChanged(int index, ItemStack previousContents) {
        this.contentsChangedListener.accept(index, previousContents);
    }
}
