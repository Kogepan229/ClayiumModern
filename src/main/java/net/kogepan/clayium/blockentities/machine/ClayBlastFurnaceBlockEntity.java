package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.ClayInterfaceBlockEntity;
import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.BlastFurnaceRecipeLogic;
import net.kogepan.clayium.capability.ISynchronizedInterface;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.BindableValue;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClayBlastFurnaceBlockEntity extends WorkableClayContainerBlockEntity {

    private static final int STRUCTURE_CHECK_INTERVAL = 20;
    private static final int STRUCTURE_PARTS = 17;

    @Getter
    private boolean structureFormed = false;
    @Getter
    private int structureRecipeTier = 0;
    private int structureCheckTimer = 0;

    public ClayBlastFurnaceBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CLAY_BLAST_FURNACE_BLOCK_ENTITY.get(), pos, blockState,
                getValidInputModes(2),
                getValidOutputModes(2),
                2,
                2,
                (be) -> new BlastFurnaceRecipeLogic(
                        be,
                        ClayiumRecipeTypes.CLAY_BLAST_FURNACE_RECIPE_TYPE.get(),
                        be.getEnergyHolder(),
                        () -> ((ClayBlastFurnaceBlockEntity) be).isStructureFormed(),
                        () -> ((ClayBlastFurnaceBlockEntity) be).getStructureRecipeTier()),
                ClayiumRecipeTypes.CLAY_BLAST_FURNACE_RECIPE_TYPE.get());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide()) {
            verifyStructure();
        }
    }

    @Override
    protected void tick() {
        if (this.level != null && !this.level.isClientSide()) {
            this.structureCheckTimer++;
            if (this.structureCheckTimer >= STRUCTURE_CHECK_INTERVAL) {
                this.structureCheckTimer = 0;
                verifyStructure();
            }
        }
        super.tick();
    }

    private void verifyStructure() {
        if (this.level == null) {
            return;
        }

        StructureCheckResult result = scanStructure(this.level);
        boolean previousFormed = this.structureFormed;
        int previousTier = this.structureRecipeTier;

        this.structureFormed = result.valid();
        this.structureRecipeTier = result.recipeTier();

        if (this.structureFormed) {
            autoLinkInterfaces(result.interfacePositions());
        } else {
            if (previousFormed) {
                clearMultiblockLinksToThisController();
            }
            if (this.recipeLogic instanceof BlastFurnaceRecipeLogic blastLogic) {
                blastLogic.invalidateProgress();
            }
        }

        if (previousFormed != this.structureFormed || previousTier != this.structureRecipeTier) {
            this.setChanged();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    private void autoLinkInterfaces(@NotNull List<BlockPos> interfacePositions) {
        if (this.level == null) {
            return;
        }
        GlobalPos controllerTarget = GlobalPos.of(this.level.dimension(), this.worldPosition);
        for (BlockPos interfacePos : interfacePositions) {
            BlockEntity blockEntity = this.level.getBlockEntity(interfacePos);
            if (blockEntity instanceof ClayInterfaceBlockEntity clayInterface) {
                clayInterface.setLinkedTarget(controllerTarget, ISynchronizedInterface.LinkSource.MULTIBLOCK);
            }
        }
    }

    private void clearMultiblockLinksToThisController() {
        if (this.level == null) {
            return;
        }
        GlobalPos controllerTarget = GlobalPos.of(this.level.dimension(), this.worldPosition);
        forEachStructurePart(this.level, (partPos) -> {
            BlockEntity blockEntity = this.level.getBlockEntity(partPos);
            if (blockEntity instanceof ClayInterfaceBlockEntity clayInterface) {
                clayInterface.clearMultiblockLinkTo(controllerTarget);
            }
        });
    }

    @NotNull
    private StructureCheckResult scanStructure(@NotNull Level level) {
        int count = 0;
        double sum = 0.0D;
        List<BlockPos> interfacePositions = new ArrayList<>();

        for (int yy = 0; yy <= 1; yy++) {
            for (int xx = -1; xx <= 1; xx++) {
                for (int zz = 0; zz <= 2; zz++) {
                    if (xx == 0 && yy == 0 && zz == 0) {
                        continue;
                    }
                    count++;
                    BlockPos partPos = toWorldPos(xx, yy, zz);
                    BlockState state = level.getBlockState(partPos);
                    BlockEntity blockEntity = level.getBlockEntity(partPos);

                    int partTier = getPartTier(state, blockEntity);
                    if (partTier <= 4) {
                        return StructureCheckResult.invalid();
                    }

                    if (blockEntity instanceof ClayInterfaceBlockEntity) {
                        interfacePositions.add(partPos.immutable());
                    }

                    sum += Math.pow(2.0D, 16 - partTier);
                }
            }
        }

        if (count != STRUCTURE_PARTS) {
            return StructureCheckResult.invalid();
        }
        int recipeTier = calculateRecipeTier(sum, count);
        return new StructureCheckResult(true, recipeTier, interfacePositions);
    }

    private int getPartTier(@NotNull BlockState partState, BlockEntity partBlockEntity) {
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

    private int calculateRecipeTier(double sum, int count) {
        int tier = (int) (16.0D - Math.floor(Math.log(sum / count) / Math.log(2.0D) + 0.5D));
        return Math.max(tier, 0);
    }

    private void forEachStructurePart(@NotNull Level level, @NotNull Consumer<BlockPos> consumer) {
        if (level != this.level) {
            return;
        }
        for (int yy = 0; yy <= 1; yy++) {
            for (int xx = -1; xx <= 1; xx++) {
                for (int zz = 0; zz <= 2; zz++) {
                    if (xx == 0 && yy == 0 && zz == 0) {
                        continue;
                    }
                    consumer.accept(toWorldPos(xx, yy, zz));
                }
            }
        }
    }

    @NotNull
    private BlockPos toWorldPos(int xx, int yy, int zz) {
        BlockState state = this.getBlockState();
        Direction facing = Direction.NORTH;
        if (state.getBlock() instanceof net.kogepan.clayium.blocks.ClayContainerBlock containerBlock) {
            facing = state.getValue(containerBlock.getFacingProperty());
        }
        Direction right = facing.getClockWise();
        Direction up = Direction.UP;
        Direction back = facing.getOpposite();

        int x = this.worldPosition.getX() + right.getStepX() * xx + up.getStepX() * yy + back.getStepX() * zz;
        int y = this.worldPosition.getY() + right.getStepY() * xx + up.getStepY() * yy + back.getStepY() * zz;
        int z = this.worldPosition.getZ() + right.getStepZ() * xx + up.getStepZ() * yy + back.getStepZ() * zz;
        return new BlockPos(x, y, z);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("structureFormed", this.structureFormed);
        tag.putInt("structureRecipeTier", this.structureRecipeTier);
        tag.putInt("structureCheckTimer", this.structureCheckTimer);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("structureFormed")) {
            this.structureFormed = tag.getBoolean("structureFormed");
        }
        if (tag.contains("structureRecipeTier")) {
            this.structureRecipeTier = tag.getInt("structureRecipeTier");
        }
        if (tag.contains("structureCheckTimer")) {
            this.structureCheckTimer = tag.getInt("structureCheckTimer");
        }
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        super.createMainUI(holder, root);
        root.addChild(new BindableValue<Boolean>().bind(DataBindingBuilder.boolS2C(() -> this.structureFormed)
                .remoteSetter(val -> this.structureFormed = val).build()));
        root.addChild(new BindableValue<Integer>().bind(DataBindingBuilder.intValS2C(() -> this.structureRecipeTier)
                .remoteSetter(val -> this.structureRecipeTier = val).build()));

        root.addChild(new CLabel().bindDataSource(SupplierDataSource.of(() -> Component.translatable(
                this.structureFormed ? "gui.clayium.clay_blast_furnace.formed" :
                        "gui.clayium.clay_blast_furnace.not_formed"))));
        root.addChild(new CLabel().bindDataSource(SupplierDataSource.of(
                () -> Component.translatable("gui.clayium.clay_blast_furnace.recipe_tier", this.structureRecipeTier))));
    }

    private record StructureCheckResult(boolean valid, int recipeTier, @NotNull List<BlockPos> interfacePositions) {

        private static StructureCheckResult invalid() {
            return new StructureCheckResult(false, 0, List.of());
        }
    }
}
