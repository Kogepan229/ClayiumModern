package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.registries.ClayiumTags;
import net.kogepan.clayium.utils.ClayTierUtil;
import net.kogepan.clayium.utils.MachineIOMode;
import net.kogepan.clayium.utils.ProgressionRates;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import org.jetbrains.annotations.NotNull;

import static net.kogepan.clayium.registries.ClayiumBlockEntityTypes.SOLAR_CLAY_FABRICATOR_BLOCK_ENTITY;

/**
 * Solar Clay Fabricator tiers 5 / 6 / 7. Compresses clay by one tier using sky access.
 * Sand is treated as tier-2 material; lithium ingot as tier-8 (Lithium-Solar only).
 */
public class SolarClayFabricatorBlockEntity extends AbstractClayFabricatorBlockEntity {

    private static final int[] ACCEPTABLE_TIERS = new int[] { 4, 6, 9 };
    private static final float[] BASE_CRAFT_TIME = new float[] { 4.0F, 3.0F, 2.0F };
    private static final float[] EFFICIENCIES = new float[] { 5000.0F, 50000.0F, 3000000.0F };

    private final int acceptableClayTier;
    private final float baseCraftTime;
    private final float initCraftTimeMultiplier;

    public SolarClayFabricatorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(SOLAR_CLAY_FABRICATOR_BLOCK_ENTITY.get(), pos, blockState);

        int solarIndex = this.tier - 5;
        this.acceptableClayTier = ACCEPTABLE_TIERS[Math.min(Math.max(solarIndex, 0), ACCEPTABLE_TIERS.length - 1)];
        this.baseCraftTime = BASE_CRAFT_TIME[Math.min(Math.max(solarIndex, 0), BASE_CRAFT_TIME.length - 1)];
        this.initCraftTimeMultiplier = calcInitCraftTime(this.tier);
    }

    @Override
    public void initDefaultRoutes() {
        Direction front = Direction.NORTH;
        BlockState blockState = this.getBlockState();
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            front = blockState.getValue(containerBlock.getFacingProperty());
        }
        this.inputModes.setMode(front.getOpposite(), MachineIOMode.ALL);
        this.outputModes.setMode(front, MachineIOMode.ALL);
    }

    private static float calcInitCraftTime(int blockTier) {
        int idx = blockTier - 5;
        if (idx < 0 || idx >= ACCEPTABLE_TIERS.length) {
            return 1.0F;
        }
        double acceptable = ACCEPTABLE_TIERS[idx];
        double base = BASE_CRAFT_TIME[idx];
        double numerator = Math.pow(10.0D, acceptable + 1.0D) * (base - 1.0D);
        double denominator = base * (Math.pow(base, acceptable) - 1.0D);
        return (float) (numerator / denominator * (20.0D / ProgressionRates.multiplyDouble(EFFICIENCIES[idx])));
    }

    protected boolean requiresSkyView() {
        return true;
    }

    protected boolean allowSandAndLithiumShortcuts() {
        return true;
    }

    @Override
    protected boolean isValidInput(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        int tier = getSolarMaterialTier(stack);
        return tier >= 0 && tier <= this.acceptableClayTier;
    }

    @Override
    protected boolean canCraftAtCurrentPosition() {
        return !this.requiresSkyView() || this.canSeeSkyForCraft();
    }

    private boolean canSeeSkyForCraft() {
        Level level = this.level;
        return level != null && level.canSeeSky(this.worldPosition.above());
    }

    @Override
    protected void tryStartCraft() {
        ItemStack in = this.inputInventory.getStackInSlot(0);
        if (in.isEmpty()) {
            return;
        }

        int materialTier = getSolarMaterialTier(in);
        if (materialTier < 0 || materialTier > this.acceptableClayTier) {
            return;
        }

        ItemStack result = ClayTierUtil.createClayStack(materialTier + 1, 1);
        if (!this.canFitOutput(result)) {
            return;
        }

        long duration = computeCraftDurationTicks(materialTier);

        ItemStack taken = in.split(1);
        if (taken.isEmpty()) {
            return;
        }

        this.startProcessing(taken, duration);
    }

    private long computeCraftDurationTicks(int materialTier) {
        double mult = this.initCraftTimeMultiplier;
        return (long) (Math.pow(this.baseCraftTime, materialTier) * mult);
    }

    @Override
    protected void advanceCraft() {
        int matTier = getSolarMaterialTier(this.processingStack);
        if (matTier < 0) {
            this.abortProcessing(true);
            return;
        }

        this.craftProgress++;
        this.displayCraftEnergy = (long) (Math.pow(10.0D, matTier + 1.0D) * (double) this.craftProgress /
                (double) this.craftDuration);

        if (this.craftProgress >= this.craftDuration) {
            ItemStack result = ClayTierUtil.createClayStack(matTier + 1, 1);
            ItemStack leftover = ItemHandlerHelper.insertItemStacked(this.outputInventory, result, false);
            if (!leftover.isEmpty()) {
                this.craftProgress = this.craftDuration - 1;
                return;
            }
            this.finishProcessing();
        }

        this.setChanged();
    }

    private int getSolarMaterialTier(@NotNull ItemStack stack) {
        int clay = ClayTierUtil.getClayTier(stack);
        if (clay >= 0) {
            return clay;
        }
        if (!this.allowSandAndLithiumShortcuts()) {
            return -1;
        }
        if (stack.getItem() == Blocks.SAND.asItem()) {
            return 2;
        }
        if (stack.is(ClayiumTags.LITHIUM_INGOTS)) {
            return 8;
        }
        return -1;
    }
}
