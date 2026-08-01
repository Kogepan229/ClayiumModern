package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.utils.ClayTierUtil;
import net.kogepan.clayium.utils.ProgressionRates;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import org.jetbrains.annotations.NotNull;

import static net.kogepan.clayium.registries.ClayiumBlockEntityTypes.CLAY_FABRICATOR_BLOCK_ENTITY;

/**
 * Clay Fabricator tiers 8 / 9 / 13. Duplicates the same clay tier; no sky requirement.
 */
public class ClayFabricatorBlockEntity extends AbstractClayFabricatorBlockEntity {

    private final int acceptableClayTier;
    private final float baseCraftTime;
    private final float stackExponent;
    private final float initCraftTimeMultiplier;

    public ClayFabricatorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(CLAY_FABRICATOR_BLOCK_ENTITY.get(), pos, blockState);

        if (this.tier >= 13) {
            this.acceptableClayTier = 13;
            this.baseCraftTime = 1.3F;
            this.stackExponent = 0.06F;
            this.initCraftTimeMultiplier = calcClayInitCraftTimeMultiplier(13, 1.3F, 0.06F, 1.0E12F);
        } else if (this.tier >= 9) {
            this.acceptableClayTier = 13;
            this.baseCraftTime = 2.0F;
            this.stackExponent = 0.3F;
            this.initCraftTimeMultiplier = calcClayInitCraftTimeMultiplier(13, 2.0F, 0.3F, 1.0E9F);
        } else {
            this.acceptableClayTier = 11;
            this.baseCraftTime = 5.0F;
            this.stackExponent = 0.85F;
            this.initCraftTimeMultiplier = calcClayInitCraftTimeMultiplier(11, 5.0F, 0.85F, 4.5E7F);
        }
    }

    private static float calcClayInitCraftTimeMultiplier(int acceptableTier, float baseCraft, float exponent,
                                                         double efficiency) {
        return (float) (Math.pow(10.0D, acceptableTier) * 64.0D /
                (Math.pow(baseCraft, acceptableTier) * Math.pow(64.0D, exponent)) *
                (20.0D / ProgressionRates.multiplyDouble(efficiency)));
    }

    @Override
    protected boolean isValidInput(@NotNull ItemStack stack) {
        int tier = ClayTierUtil.getClayTier(stack);
        return tier >= 0 && tier <= this.acceptableClayTier;
    }

    @Override
    protected void tryStartCraft() {
        ItemStack in = this.inputInventory.getStackInSlot(0);
        if (in.isEmpty()) {
            return;
        }

        int clayTier = ClayTierUtil.getClayTier(in);
        if (clayTier < 0 || clayTier > this.acceptableClayTier) {
            return;
        }

        int count = in.getCount();
        ItemStack result = ClayTierUtil.createClayStack(clayTier, count);
        if (!this.canFitOutput(result)) {
            return;
        }

        long duration = computeDuplicationDurationTicks(clayTier, count);
        this.startProcessing(in.copy(), duration);
    }

    private long computeDuplicationDurationTicks(int clayTier, int stackCount) {
        if (clayTier == 0) {
            return 1L;
        }
        double mult = this.initCraftTimeMultiplier;
        return (long) (Math.pow(this.baseCraftTime, clayTier) * Math.pow(stackCount, this.stackExponent) * mult);
    }

    @Override
    protected void advanceCraft() {
        int clayTier = ClayTierUtil.getClayTier(this.processingStack);
        if (clayTier < 0) {
            this.abortProcessing(false);
            return;
        }

        int count = this.processingStack.getCount();
        this.craftProgress++;
        this.displayCraftEnergy = (long) (Math.pow(10.0D, clayTier) * (double) count * (double) this.craftProgress /
                (double) this.craftDuration);

        if (this.craftProgress >= this.craftDuration) {
            ItemStack result = ClayTierUtil.createClayStack(clayTier, count);
            ItemStack leftover = ItemHandlerHelper.insertItemStacked(this.outputInventory, result, false);
            if (!leftover.isEmpty()) {
                this.craftProgress = this.craftDuration - 1;
                return;
            }
            this.finishProcessing();
        }

        this.setChanged();
    }
}
