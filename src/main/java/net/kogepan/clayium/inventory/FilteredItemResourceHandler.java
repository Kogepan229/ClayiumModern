package net.kogepan.clayium.inventory;

import net.kogepan.clayium.capability.filter.data.ItemFilterData;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;

/** Item handler view that rejects insertion and extraction when a face filter does not match. */
public final class FilteredItemResourceHandler extends DelegatingResourceHandler<ItemResource> {

    private final ItemFilterData filter;

    public FilteredItemResourceHandler(ResourceHandler<ItemResource> delegate, ItemFilterData filter) {
        super(delegate);
        this.filter = Objects.requireNonNull(filter);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return resource.isEmpty() || this.matches(resource) && super.isValid(index, resource);
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        return this.matches(resource) ? super.insert(index, resource, amount, transaction) : 0;
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        return this.matches(resource) ? super.insert(resource, amount, transaction) : 0;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        return this.matches(resource) ? super.extract(index, resource, amount, transaction) : 0;
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        return this.matches(resource) ? super.extract(resource, amount, transaction) : 0;
    }

    private boolean matches(ItemResource resource) {
        return !resource.isEmpty() && resource.test(this.filter::test);
    }
}
