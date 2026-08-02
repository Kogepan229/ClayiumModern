package net.kogepan.clayium.utils;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

/** Transactional helpers for operations that span multiple item stacks. */
public final class TransferUtils {

    private TransferUtils() {}

    /**
     * Checks whether every stack can be inserted together, without changing the handler.
     *
     * @param handler           destination handler
     * @param stacks            stacks to insert in iteration order
     * @param parentTransaction parent transaction, or {@code null} to open a root transaction
     * @return {@code true} when every non-empty stack fits
     */
    public static boolean canInsertAllItems(
                                            ResourceHandler<ItemResource> handler,
                                            Iterable<ItemStack> stacks,
                                            @Nullable TransactionContext parentTransaction) {
        return insertAllItems(handler, stacks, parentTransaction, false);
    }

    /**
     * Inserts every stack atomically, or rolls back the complete operation when one does not fit.
     *
     * <p>
     * When a parent transaction is supplied, success commits only the nested transaction. The caller still decides
     * whether to commit or roll back the parent transaction.
     *
     * @param handler           destination handler
     * @param stacks            stacks to insert in iteration order
     * @param parentTransaction parent transaction, or {@code null} to open and commit a root transaction
     * @return {@code true} when every non-empty stack was inserted
     */
    public static boolean insertAllItems(
                                         ResourceHandler<ItemResource> handler,
                                         Iterable<ItemStack> stacks,
                                         @Nullable TransactionContext parentTransaction) {
        return insertAllItems(handler, stacks, parentTransaction, true);
    }

    private static boolean insertAllItems(
                                          ResourceHandler<ItemResource> handler,
                                          Iterable<ItemStack> stacks,
                                          @Nullable TransactionContext parentTransaction,
                                          boolean commit) {
        Objects.requireNonNull(handler);
        Objects.requireNonNull(stacks);

        try (Transaction transaction = Transaction.open(parentTransaction)) {
            for (ItemStack stack : stacks) {
                Objects.requireNonNull(stack);
                if (stack.isEmpty()) {
                    continue;
                }

                int inserted = ResourceHandlerUtil.insertStacking(
                        handler,
                        ItemResource.of(stack),
                        stack.getCount(),
                        transaction);
                if (inserted != stack.getCount()) {
                    return false;
                }
            }

            if (commit) {
                transaction.commit();
            }
            return true;
        }
    }
}
