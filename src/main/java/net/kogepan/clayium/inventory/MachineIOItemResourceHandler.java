package net.kogepan.clayium.inventory;

import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.utils.MachineIOMode;

import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

/** Combined machine item view that permits insertion into inputs and extraction from outputs. */
public final class MachineIOItemResourceHandler implements ResourceHandler<ItemResource> {

    private final @Nullable ResourceHandler<ItemResource> input;
    private final @Nullable ResourceHandler<ItemResource> output;
    private final int inputSize;
    private final int size;

    private MachineIOItemResourceHandler(
                                         @Nullable ResourceHandler<ItemResource> input,
                                         @Nullable ResourceHandler<ItemResource> output) {
        this.input = input;
        this.output = output;
        this.inputSize = input != null ? input.size() : 0;
        this.size = this.inputSize + (output != null ? output.size() : 0);
    }

    /**
     * Creates an item capability view for one face of a machine.
     *
     * @param input      complete machine input storage
     * @param output     complete machine output storage
     * @param inputMode  input route selected for the face
     * @param outputMode output route selected for the face
     * @param filter     optional filter applied to both transfer directions
     * @return a stable view for the selected routes
     */
    public static ResourceHandler<ItemResource> create(
                                                       ResourceHandler<ItemResource> input,
                                                       ResourceHandler<ItemResource> output,
                                                       MachineIOMode inputMode,
                                                       MachineIOMode outputMode,
                                                       @Nullable ItemFilterData filter) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(output);
        Objects.requireNonNull(inputMode);
        Objects.requireNonNull(outputMode);

        ResourceHandler<ItemResource> handler = new MachineIOItemResourceHandler(
                selectRoute(input, inputMode),
                selectRoute(output, outputMode));
        return filter != null ? new FilteredItemResourceHandler(handler, filter) : handler;
    }

    private static @Nullable ResourceHandler<ItemResource> selectRoute(
                                                                       ResourceHandler<ItemResource> handler,
                                                                       MachineIOMode mode) {
        return switch (mode) {
            case FIRST -> handler.size() >= 1 ? RangedResourceHandler.ofSingleIndex(handler, 0) : null;
            case SECOND -> handler.size() >= 2 ? RangedResourceHandler.ofSingleIndex(handler, 1) : null;
            case ALL -> handler;
            default -> null;
        };
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ItemResource getResource(int index) {
        LocatedIndex located = this.locate(index);
        return located.handler().getResource(located.index());
    }

    @Override
    public long getAmountAsLong(int index) {
        LocatedIndex located = this.locate(index);
        return located.handler().getAmountAsLong(located.index());
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        LocatedIndex located = this.locate(index);
        return located.handler().getCapacityAsLong(located.index(), resource);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        LocatedIndex located = this.locate(index);
        return located.input() && located.handler().isValid(located.index(), resource);
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        LocatedIndex located = this.locate(index);
        return located.input() ? located.handler().insert(located.index(), resource, amount, transaction) : 0;
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        return this.input != null ? this.input.insert(resource, amount, transaction) : 0;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        LocatedIndex located = this.locate(index);
        return !located.input() ? located.handler().extract(located.index(), resource, amount, transaction) : 0;
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        return this.output != null ? this.output.extract(resource, amount, transaction) : 0;
    }

    private LocatedIndex locate(int index) {
        Objects.checkIndex(index, this.size);
        if (index < this.inputSize) {
            return new LocatedIndex(Objects.requireNonNull(this.input), index, true);
        }
        return new LocatedIndex(Objects.requireNonNull(this.output), index - this.inputSize, false);
    }

    private record LocatedIndex(ResourceHandler<ItemResource> handler, int index, boolean input) {}
}
