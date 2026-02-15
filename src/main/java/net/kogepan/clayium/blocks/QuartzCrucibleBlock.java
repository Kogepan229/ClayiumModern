package net.kogepan.clayium.blocks;

import net.kogepan.clayium.blockentities.QuartzCrucibleBlockEntity;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.recipes.QuartzCrucibleRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
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
 * Quartz Crucible: processes items according to JSON recipes. Drop input items into the block,
 * wait for the configured time (ticks per unit), then drop the catalyst item to collect results.
 * Max 9 units at once. Recipes define input, catalyst, result, and duration (ticks per unit).
 */
public class QuartzCrucibleBlock extends Block implements EntityBlock {

    public static final int MAX_LEVEL = 9;

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

        // Try input: find a recipe that accepts this item as input
        int currentLevel = state.getValue(LEVEL);
        if (currentLevel < MAX_LEVEL) {
            for (RecipeHolder<QuartzCrucibleRecipe> holder : level.getRecipeManager()
                    .getAllRecipesFor(ClayiumRecipeTypes.QUARTZ_CRUCIBLE_RECIPE_TYPE.get())) {
                QuartzCrucibleRecipe recipe = holder.value();
                if (!recipe.input().test(stack)) {
                    continue;
                }
                // Same recipe type required if we already started
                ResourceLocation currentStoredId = crucible.getCurrentRecipeId();
                if (crucible.getIngotQuantity() > 0 && currentStoredId != null &&
                        !currentStoredId.equals(holder.id())) {
                    continue;
                }
                stack.shrink(1);
                if (stack.isEmpty()) {
                    itemEntity.discard();
                }
                if (crucible.getIngotQuantity() == 0) {
                    crucible.setRecipeAndAddIngot(holder);
                } else {
                    crucible.addIngot();
                }
                level.setBlock(pos, state.setValue(LEVEL, currentLevel + 1), Block.UPDATE_ALL);
                return;
            }
        }

        // Try catalyst: current recipe must use this item as catalyst and progress must be complete
        QuartzCrucibleRecipe recipe = crucible.getCurrentRecipe();
        if (recipe != null && currentLevel > 0 && recipe.catalyst().test(stack) &&
                crucible.getProgress() >= crucible.getRequiredTicks()) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                itemEntity.discard();
            }
            ItemStack result = recipe.getResultForQuantity(crucible.getIngotQuantity());
            Block.popResource(level, pos, result);
            crucible.reset();
            level.setBlock(pos, state.setValue(LEVEL, 0), Block.UPDATE_ALL);
        }
    }
}
