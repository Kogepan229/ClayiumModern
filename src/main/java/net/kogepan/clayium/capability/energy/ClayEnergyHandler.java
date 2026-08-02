package net.kogepan.clayium.capability.energy;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/** A transactional handler for Clay Energy (CE), supporting long transfer amounts. */
public interface ClayEnergyHandler extends EnergyHandler {

    /** Inserts up to the given amount of Clay Energy. */
    long insert(long amount, TransactionContext transaction);

    /** Extracts up to the given amount of Clay Energy. */
    long extract(long amount, TransactionContext transaction);
}
