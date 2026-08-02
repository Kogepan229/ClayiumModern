package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.capability.energy.ClayEnergyHandler;
import net.kogepan.clayium.capability.energy.ClayEnergyStorage;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class ClayEnergyHolder extends ClayContainerTrait implements ClayEnergyHandler {

    public static final String TRAIT_ID = "clayEnergyHolder";

    private final ClayEnergyStorage storage;

    public ClayEnergyHolder(ClayContainerBlockEntity blockEntity) {
        super(blockEntity, TRAIT_ID);
        this.storage = new ClayEnergyStorage(previousAmount -> blockEntity.setChanged());
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        this.storage.serialize(output);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        this.storage.deserialize(input);
    }

    @Override
    public long getAmountAsLong() {
        return this.storage.getAmountAsLong();
    }

    @Override
    public long getCapacityAsLong() {
        return this.storage.getCapacityAsLong();
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        return this.storage.insert(amount, transaction);
    }

    @Override
    public long insert(long amount, TransactionContext transaction) {
        return this.storage.insert(amount, transaction);
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        return this.storage.extract(amount, transaction);
    }

    @Override
    public long extract(long amount, TransactionContext transaction) {
        return this.storage.extract(amount, transaction);
    }
}
