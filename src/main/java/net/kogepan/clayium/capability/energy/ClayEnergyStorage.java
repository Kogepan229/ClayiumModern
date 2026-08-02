package net.kogepan.clayium.capability.energy;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;
import java.util.function.LongConsumer;

/** Long-capacity Clay Energy storage with commit-time change notifications. */
public class ClayEnergyStorage implements ClayEnergyHandler, ValueIOSerializable {

    private static final String ENERGY_KEY = "clayEnergy";
    private static final LongConsumer NO_OP_LISTENER = previousAmount -> {};

    private final EnergyJournal energyJournal = new EnergyJournal();
    private final LongConsumer energyChangedListener;

    protected long energy;
    protected long capacity;
    protected long maxInsert;
    protected long maxExtract;

    public ClayEnergyStorage() {
        this(NO_OP_LISTENER);
    }

    public ClayEnergyStorage(LongConsumer energyChangedListener) {
        this(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, 0L, energyChangedListener);
    }

    public ClayEnergyStorage(
                             long capacity,
                             long maxInsert,
                             long maxExtract,
                             long energy,
                             LongConsumer energyChangedListener) {
        checkNonNegative(capacity);
        checkNonNegative(maxInsert);
        checkNonNegative(maxExtract);
        checkNonNegative(energy);

        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
        this.energy = energy;
        this.energyChangedListener = Objects.requireNonNull(energyChangedListener);
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putLong(ENERGY_KEY, this.energy);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.energy = Math.max(0L, input.getLongOr(ENERGY_KEY, 0L));
    }

    /** Directly replaces the stored amount and immediately notifies the listener. */
    public void set(long amount) {
        checkNonNegative(amount);
        if (this.energy != amount) {
            long previousAmount = this.energy;
            this.energy = amount;
            this.energyChangedListener.accept(previousAmount);
        }
    }

    @Override
    public long getAmountAsLong() {
        return this.energy;
    }

    @Override
    public long getCapacityAsLong() {
        return this.capacity;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        return (int) this.insert((long) amount, transaction);
    }

    @Override
    public long insert(long amount, TransactionContext transaction) {
        checkNonNegative(amount);
        Objects.requireNonNull(transaction);

        long availableCapacity = Math.max(0L, this.capacity - this.energy);
        long inserted = Math.min(availableCapacity, Math.min(amount, this.maxInsert));
        if (inserted > 0L) {
            this.energyJournal.updateSnapshots(transaction);
            this.energy += inserted;
        }
        return inserted;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        return (int) this.extract((long) amount, transaction);
    }

    @Override
    public long extract(long amount, TransactionContext transaction) {
        checkNonNegative(amount);
        Objects.requireNonNull(transaction);

        long extracted = Math.min(this.energy, Math.min(amount, this.maxExtract));
        if (extracted > 0L) {
            this.energyJournal.updateSnapshots(transaction);
            this.energy -= extracted;
        }
        return extracted;
    }

    private static void checkNonNegative(long value) {
        if (value < 0L) {
            throw new IllegalArgumentException("Expected value to be non-negative: " + value);
        }
    }

    private final class EnergyJournal extends SnapshotJournal<Long> {

        @Override
        protected Long createSnapshot() {
            return energy;
        }

        @Override
        protected void revertToSnapshot(Long snapshot) {
            energy = snapshot;
        }

        @Override
        protected void onRootCommit(Long originalState) {
            if (energy != originalState) {
                energyChangedListener.accept(originalState);
            }
        }
    }
}
