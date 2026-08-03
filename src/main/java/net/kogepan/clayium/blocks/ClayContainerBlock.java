package net.kogepan.clayium.blocks;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import org.jspecify.annotations.Nullable;

public abstract class ClayContainerBlock extends Block implements EntityBlock, BlockUIMenuType.BlockUI {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty PIPE = BooleanProperty.create("pipe");
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape ARM_NORTH = Block.box(5, 5, 0, 11, 11, 5);
    private static final VoxelShape ARM_SOUTH = Block.box(5, 5, 11, 11, 11, 16);
    private static final VoxelShape ARM_WEST = Block.box(0, 5, 5, 5, 11, 11);
    private static final VoxelShape ARM_EAST = Block.box(11, 5, 5, 16, 11, 11);
    private static final VoxelShape ARM_UP = Block.box(5, 11, 5, 11, 16, 11);
    private static final VoxelShape ARM_DOWN = Block.box(5, 0, 5, 11, 5, 11);

    private final int tier;

    protected ClayContainerBlock(int tier, BlockBehaviour.Properties properties) {
        super(properties.dynamicShape().forceSolidOn());
        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PIPE, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    public final int tier() {
        return this.tier;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(
                            Level level,
                            BlockPos pos,
                            BlockState state,
                            @Nullable LivingEntity placer,
                            ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ClayContainerBlockEntity container) {
            container.onPlacedByServer();
        }
    }

    @Override
    protected InteractionResult useWithoutItem(
                                               BlockState state,
                                               Level level,
                                               BlockPos pos,
                                               Player player,
                                               BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            BlockUIMenuType.openUI(serverPlayer, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        if (holder.player.level().getBlockEntity(holder.pos) instanceof ClayContainerBlockEntity entity) {
            return entity.createUI(holder);
        }
        return new ModularUI(UI.empty(), holder.player);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PIPE, NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    protected BlockState updateShape(
                                     BlockState state,
                                     LevelReader level,
                                     ScheduledTickAccess ticks,
                                     BlockPos pos,
                                     Direction directionToNeighbour,
                                     BlockPos neighbourPos,
                                     BlockState neighbourState,
                                     RandomSource random) {
        if (level.isClientSide() || !state.getValue(PIPE)) {
            return state;
        }
        if (level.getBlockEntity(pos) instanceof ClayContainerBlockEntity container) {
            return state.setValue(getConnectionProperty(directionToNeighbour),
                    container.canConnectTo(directionToNeighbour));
        }
        return state;
    }

    @Override
    protected VoxelShape getShape(
                                  BlockState state,
                                  net.minecraft.world.level.BlockGetter level,
                                  BlockPos pos,
                                  CollisionContext context) {
        if (!state.getValue(PIPE)) {
            return super.getShape(state, level, pos, context);
        }

        VoxelShape shape = CORE;
        for (Direction direction : Direction.values()) {
            if (state.getValue(getConnectionProperty(direction))) {
                shape = Shapes.or(shape, getArmShape(direction));
            }
        }
        return shape;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return state.getValue(PIPE) ? Shapes.empty() : super.getOcclusionShape(state);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return state.getValue(PIPE);
    }

    public static Direction getConfigurationSide(BlockState state, BlockPos pos, Vec3 hit, Direction hitFace) {
        if (!state.getValue(PIPE)) {
            return hitFace;
        }
        Vec3 localHit = hit.subtract(pos.getX(), pos.getY(), pos.getZ());
        for (Direction direction : Direction.values()) {
            if (getArmShape(direction).bounds().contains(localHit)) {
                return direction;
            }
        }
        return hitFace;
    }

    public static BooleanProperty getConnectionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    private static VoxelShape getArmShape(Direction direction) {
        return switch (direction) {
            case NORTH -> ARM_NORTH;
            case SOUTH -> ARM_SOUTH;
            case EAST -> ARM_EAST;
            case WEST -> ARM_WEST;
            case UP -> ARM_UP;
            case DOWN -> ARM_DOWN;
        };
    }

    @SuppressWarnings("unchecked")
    protected static <E extends BlockEntity, A extends BlockEntity> @Nullable BlockEntityTicker<A> createTickerHelper(
                                                                                                                      BlockEntityType<A> actual,
                                                                                                                      BlockEntityType<E> expected,
                                                                                                                      BlockEntityTicker<? super E> ticker) {
        return actual == expected ? (BlockEntityTicker<A>) ticker : null;
    }
}
