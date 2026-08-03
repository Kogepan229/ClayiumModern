package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.ClayInterfaceBlockEntity;
import net.kogepan.clayium.blockentities.trait.MultiblockRecipeLogic;
import net.kogepan.clayium.blocks.OverclockerBlock;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClayBlastFurnaceBlockEntity extends AbstractMultiblockMachineBlockEntity {

    private static final int STRUCTURE_PARTS = 17;

    public ClayBlastFurnaceBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CLAY_BLAST_FURNACE_BLOCK_ENTITY.get(), pos, blockState,
                getValidInputModes(2),
                getValidOutputModes(2),
                2,
                2,
                be -> new MultiblockRecipeLogic(
                        be,
                        ClayiumRecipeTypes.CLAY_BLAST_FURNACE_RECIPE_TYPE.get(),
                        be.getEnergyHolder(),
                        () -> ((ClayBlastFurnaceBlockEntity) be).isStructureFormed(),
                        () -> ((ClayBlastFurnaceBlockEntity) be).getStructureRecipeTier()),
                ClayiumRecipeTypes.CLAY_BLAST_FURNACE_RECIPE_TYPE.get());
    }

    @Override
    @NotNull
    protected StructureCheckResult scanStructure(@NotNull Level level) {
        int count = 0;
        int weightSum = 0;
        List<BlockPos> interfacePositions = new ArrayList<>();

        for (int yy = 0; yy <= 1; yy++) {
            for (int xx = -1; xx <= 1; xx++) {
                for (int zz = 0; zz <= 2; zz++) {
                    if (xx == 0 && yy == 0 && zz == 0) {
                        continue;
                    }
                    count++;
                    BlockPos partPos = this.toWorldPos(xx, yy, zz);
                    if (!level.isLoaded(partPos)) {
                        return StructureCheckResult.invalid();
                    }
                    BlockState state = level.getBlockState(partPos);
                    BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(partPos) : null;

                    int partTier = this.getPartTier(state, blockEntity);
                    if (partTier <= 4) {
                        return StructureCheckResult.invalid();
                    }

                    if (blockEntity instanceof ClayInterfaceBlockEntity) {
                        interfacePositions.add(partPos.immutable());
                    }

                    weightSum += (int) Math.pow(2.0D, 16 - partTier);
                }
            }
        }

        if (count != STRUCTURE_PARTS) {
            return StructureCheckResult.invalid();
        }
        return new StructureCheckResult(true, calculateStructureTier(weightSum, count), interfacePositions);
    }

    private int getPartTier(@NotNull BlockState partState, BlockEntity partBlockEntity) {
        if (partState.getBlock() instanceof OverclockerBlock overclocker && overclocker.getTier() >= 5 &&
                overclocker.getTier() <= 13) {
            return overclocker.getTier();
        }
        int hullTier = ClayiumBlocks.getMachineHullTier(partState.getBlock());
        if (hullTier >= 5) {
            return hullTier;
        }
        if (partBlockEntity instanceof ClayInterfaceBlockEntity clayInterface) {
            int interfaceTier = clayInterface.tier;
            if (interfaceTier >= 5 && interfaceTier <= 13) {
                return interfaceTier;
            }
        }
        return -1;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        super.createMainUI(holder, root);
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(() -> Component.translatable(
                this.isStructureFormed() ? "gui.clayium.clay_blast_furnace.formed" :
                        "gui.clayium.clay_blast_furnace.not_formed"))
                .build()));
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(
                () -> Component.translatable(
                        "gui.clayium.clay_blast_furnace.recipe_tier", this.getStructureRecipeTier()))
                .build()));
    }
}
