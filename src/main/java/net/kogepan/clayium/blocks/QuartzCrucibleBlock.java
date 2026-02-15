package net.kogepan.clayium.blocks;

import net.kogepan.clayium.blockentities.QuartzCrucibleBlockEntity;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Quartz Crucible: purifies impure silicon ingots into silicon ingots. Drop impure silicon ingots
 * into the block, wait for the process (30 seconds per ingot), then drop a string to collect silicon
 * ingots. Max 9 ingots at once.
 */
public class QuartzCrucibleBlock extends Block implements EntityBlock {

    public static final int MAX_LEVEL = 9;
    public static final int TICKS_PER_INGOT = 600; // 30 seconds at 20 TPS

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, MAX_LEVEL);

    private static final VoxelShape BASE = Block.box(0, 0, 0, 16, 1.08, 16);
    private static final VoxelShape WALL_NORTH = Block.box(0, 1, 0, 16, 12, 1.08);
    private static final VoxelShape WALL_SOUTH = Block.box(0, 1, 14.92, 16, 12, 16);
    private static final VoxelShape WALL_EAST = Block.box(14.92, 1, 0, 16, 12, 16);
    private static final VoxelShape WALL_WEST = Block.box(0, 1, 0, 1.08, 12, 16);
    private static final VoxelShape SHAPE = Shapes.or(BASE, WALL_NORTH, WALL_SOUTH, WALL_EAST, WALL_WEST);

    public QuartzCrucibleBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.GLASS)
                .destroyTime(0.2f)
                .explosionResistance(0.2f)
                .noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new QuartzCrucibleBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return type == net.kogepan.clayium.registries.ClayiumBlockEntityTypes.QUARTZ_CRUCIBLE_BLOCK_ENTITY.get() ?
                (lvl, pos, s, be) -> QuartzCrucibleBlockEntity.tick(lvl, pos, s, (QuartzCrucibleBlockEntity) be) : null;
    }

    @Override
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                               @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    @NotNull
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                             @NotNull Entity entity) {
        if (level.isClientSide() || !(entity instanceof ItemEntity itemEntity)) {
            return;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof QuartzCrucibleBlockEntity crucible)) {
            return;
        }
        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty()) {
            return;
        }

        if (stack.is(ClayiumItems.IMPURE_SILICON_INGOT.get())) {
            int currentLevel = state.getValue(LEVEL);
            if (currentLevel >= MAX_LEVEL) {
                return;
            }
            stack.shrink(1);
            if (stack.isEmpty()) {
                itemEntity.discard();
            }
            crucible.addIngot();
            level.setBlock(pos, state.setValue(LEVEL, currentLevel + 1), Block.UPDATE_ALL);
            return;
        }

        if (stack.is(Items.STRING)) {
            int currentLevel = state.getValue(LEVEL);
            if (currentLevel <= 0) {
                return;
            }
            if (crucible.getProgress() >= TICKS_PER_INGOT * currentLevel) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    itemEntity.discard();
                }
                crucible.reset();
                ItemStack result = new ItemStack(ClayiumItems.SILICON_INGOT.get(), currentLevel);
                Block.popResource(level, pos, result);
                level.setBlock(pos, state.setValue(LEVEL, 0), Block.UPDATE_ALL);
            }
        }
    }
}
