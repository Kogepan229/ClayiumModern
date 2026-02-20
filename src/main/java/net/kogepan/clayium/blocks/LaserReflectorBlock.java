package net.kogepan.clayium.blocks;

import net.kogepan.clayium.blockentities.LaserReflectorBlockEntity;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Laser Reflector block.
 * <p>
 * Changes the direction of Clay Laser. Accepts laser from multiple faces and re-emits merged laser
 * in the facing direction.
 */
public class LaserReflectorBlock extends Block implements EntityBlock {

    private static final float F = 0.125f;
    // Block.box uses 0-16 scale. Original: from (f*2,f,f*2) to (1-f*2,1-f,1-f*2) for UP/DOWN
    private static final VoxelShape SHAPE_UP_DOWN = Block.box(
            F * 2 * 16, F * 16, F * 2 * 16,
            (1 - F * 2) * 16, (1 - F) * 16, (1 - F * 2) * 16);
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(
            F * 2 * 16, F * 2 * 16, F * 16,
            (1 - F * 2) * 16, (1 - F * 2) * 16, (1 - F) * 16);
    private static final VoxelShape SHAPE_EAST_WEST = Block.box(
            F * 16, F * 2 * 16, F * 2 * 16,
            (1 - F) * 16, (1 - F * 2) * 16, (1 - F * 2) * 16);

    public LaserReflectorBlock() {
        super(BlockBehaviour.Properties.of()
                .noOcclusion()
                .sound(SoundType.GLASS)
                .strength(1.0f, 1.0f)
                .forceSolidOn());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                               @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP, DOWN -> SHAPE_UP_DOWN;
            case NORTH, SOUTH -> SHAPE_NORTH_SOUTH;
            case EAST, WEST -> SHAPE_EAST_WEST;
        };
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new LaserReflectorBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return ClayContainerBlock.createTickerHelper(level, type,
                ClayiumBlockEntityTypes.LASER_REFLECTOR_BLOCK_ENTITY.get(),
                LaserReflectorBlockEntity::tick, null);
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                               @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide() && player.isShiftKeyDown()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LaserReflectorBlockEntity reflector) {
                Direction current = state.getValue(FACING);
                Direction next = current.getClockWise(Direction.Axis.Y);
                level.setBlock(pos, state.setValue(FACING, next), Block.UPDATE_CLIENTS);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
