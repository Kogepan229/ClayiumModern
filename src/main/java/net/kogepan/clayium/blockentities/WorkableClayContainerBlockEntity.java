package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blockentities.trait.AbstractRecipeLogic;
import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blockentities.trait.ClayEnergyHolder;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.inventory.NotifiableItemStackHandler;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public abstract class WorkableClayContainerBlockEntity extends ClayContainerBlockEntity {

    protected final NotifiableItemStackHandler inputItemInventory;
    protected final NotifiableItemStackHandler outputItemInventory;

    protected final RecipeType<?> recipeType;

    public WorkableClayContainerBlockEntity(@NotNull BlockEntityType<?> type,
                                            @NotNull BlockPos pos,
                                            @NotNull BlockState blockState,
                                            @NotNull List<MachineIOMode> validInputModes,
                                            @NotNull List<MachineIOMode> validOutputModes,
                                            int inputSize,
                                            int outputSize,
                                            Function<WorkableClayContainerBlockEntity, @NotNull AbstractRecipeLogic> recipeLogicProvider,
                                            @NotNull RecipeType<?> recipeType) {
        super(type, pos, blockState, validInputModes, validOutputModes);
        this.inputItemInventory = new NotifiableItemStackHandler(this, inputSize, true);
        this.outputItemInventory = new NotifiableItemStackHandler(this, outputSize, false);
        this.recipeType = recipeType;

        this.addTrait(new AutoIOTrait.Combined(this, this.tier, false));
        this.addTrait(new ClayEnergyHolder(this));
        this.addTrait(recipeLogicProvider.apply(this));
    }

    @Override
    public void initDefaultRoutes() {
        this.inputModes.setMode(Direction.UP, MachineIOMode.ALL);
        this.inputModes.setMode(this.getBlockState().getValue(ClayContainerBlock.FACING).getOpposite(),
                MachineIOMode.CE);
        this.outputModes.setMode(Direction.DOWN, MachineIOMode.ALL);
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return this.inputItemInventory;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.outputItemInventory;
    }

    private static final List<List<MachineIOMode>> VALID_INPUT_MODES_LISTS = List.of(
            List.of(MachineIOMode.NONE, MachineIOMode.CE),
            List.of(MachineIOMode.ALL, MachineIOMode.CE, MachineIOMode.NONE),
            List.of(MachineIOMode.ALL, MachineIOMode.FIRST, MachineIOMode.SECOND, MachineIOMode.CE,
                    MachineIOMode.NONE));

    private static final List<List<MachineIOMode>> VALID_OUTPUT_MODES_LISTS = List.of(
            List.of(MachineIOMode.NONE),
            List.of(MachineIOMode.ALL, MachineIOMode.NONE),
            List.of(MachineIOMode.ALL, MachineIOMode.FIRST, MachineIOMode.SECOND, MachineIOMode.NONE));

    public static List<MachineIOMode> getValidInputModes(int slots) {
        if (slots < VALID_INPUT_MODES_LISTS.size()) {
            return VALID_INPUT_MODES_LISTS.get(slots);
        }
        return VALID_INPUT_MODES_LISTS.getLast();
    }

    public static List<MachineIOMode> getValidOutputModes(int slots) {
        if (slots < VALID_OUTPUT_MODES_LISTS.size()) {
            return VALID_OUTPUT_MODES_LISTS.get(slots);
        }
        return VALID_OUTPUT_MODES_LISTS.getLast();
    }
}
