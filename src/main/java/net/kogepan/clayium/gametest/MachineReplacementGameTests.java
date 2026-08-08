package net.kogepan.clayium.gametest;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.ClayBufferBlockEntity;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.AbstractRecipeLogic;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.machine.replacement.MachineReplacementService;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Collection;
import java.util.List;

@GameTestHolder(Clayium.MODID)
public final class MachineReplacementGameTests {

    private static final String BATCH = Clayium.MODID + ".machine_replacement";
    private static final String EMPTY_STRUCTURE = Clayium.MODID + ":empty";
    private static final String TEST_NAME_PREFIX = "machinereplacementgametests.";
    private static final BlockPos MACHINE_POS = new BlockPos(2, 2, 2);

    private MachineReplacementGameTests() {}

    @GameTestGenerator
    public static Collection<TestFunction> generateTests() {
        return List.of(
                test("preserves_simple_machine_state", MachineReplacementGameTests::preservesSimpleMachineState),
                test("returns_downgrade_overflow", MachineReplacementGameTests::returnsDowngradeOverflow),
                test("rolls_back_canceled_placement", MachineReplacementGameTests::rollsBackCanceledPlacement),
                test("preserves_creative_items", MachineReplacementGameTests::preservesCreativeItems),
                test("rejects_same_machine", MachineReplacementGameTests::rejectsSameMachine),
                test("rejects_active_recipe_downgrade", MachineReplacementGameTests::rejectsActiveRecipeDowngrade));
    }

    private static TestFunction test(String name, java.util.function.Consumer<GameTestHelper> test) {
        return new TestFunction(BATCH, TEST_NAME_PREFIX + name, EMPTY_STRUCTURE, 100, 0L, true, test);
    }

    private static void preservesSimpleMachineState(GameTestHelper helper) {
        ClayContainerBlock sourceBlock = ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(1).get();
        ClayContainerBlock targetBlock = ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(4).get();
        BlockState sourceState = sourceBlock.defaultBlockState()
                .setValue(sourceBlock.getFacingProperty(), Direction.SOUTH)
                .setValue(ClayContainerBlock.PIPE, true);
        helper.setBlock(MACHINE_POS, sourceState);

        WorkableClayContainerBlockEntity source = getWorkable(helper);
        source.getInputInventory().setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 12));
        source.getOutputInventory().setStackInSlot(0, new ItemStack(Items.BRICK, 3));
        source.getEnergyHolder().addEnergy(123_456L);
        source.getInputModes().setMode(Direction.UP, MachineIOMode.ALL);
        source.getOutputModes().setMode(Direction.DOWN, MachineIOMode.ALL);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        InteractionResult result = replace(helper, player, targetBlock);
        if (!result.consumesAction()) {
            helper.fail("Expected machine replacement to consume the interaction", MACHINE_POS);
            return;
        }
        if (!helper.getBlockState(MACHINE_POS).is(targetBlock)) {
            helper.fail("Expected the target machine block", MACHINE_POS);
            return;
        }

        WorkableClayContainerBlockEntity target = getWorkable(helper);
        if (target.getBlockState().getValue(targetBlock.getFacingProperty()) != Direction.SOUTH ||
                !target.getBlockState().getValue(ClayContainerBlock.PIPE)) {
            helper.fail("Machine orientation or piping state was not preserved", MACHINE_POS);
            return;
        }
        if (target.getInputInventory().getStackInSlot(0).getCount() != 12 ||
                target.getOutputInventory().getStackInSlot(0).getCount() != 3 ||
                target.getEnergyHolder().getEnergyStored() != 123_456L ||
                target.getInputMode(Direction.UP) != MachineIOMode.ALL ||
                target.getOutputMode(Direction.DOWN) != MachineIOMode.ALL) {
            helper.fail("Machine contents or configuration were not preserved", MACHINE_POS);
            return;
        }
        if (player.getInventory().countItem(targetBlock.asItem()) != 0 ||
                player.getInventory().countItem(sourceBlock.asItem()) != 1) {
            helper.fail("Survival item exchange was incorrect", MACHINE_POS);
            return;
        }
        helper.succeed();
    }

    private static void returnsDowngradeOverflow(GameTestHelper helper) {
        Block sourceBlock = ClayiumBlocks.CLAY_BUFFERS.get(13).get();
        Block targetBlock = ClayiumBlocks.CLAY_BUFFERS.get(4).get();
        helper.setBlock(MACHINE_POS, sourceBlock);

        ClayBufferBlockEntity source = getBuffer(helper);
        source.getInputInventory().setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 64));
        source.getInputInventory().setStackInSlot(1, new ItemStack(Items.CLAY_BALL, 64));
        source.getInputInventory().setStackInSlot(2, new ItemStack(Items.CLAY_BALL, 20));

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        InteractionResult result = replace(helper, player, targetBlock);
        if (!result.consumesAction()) {
            helper.fail("Expected buffer replacement to consume the interaction", MACHINE_POS);
            return;
        }

        ClayBufferBlockEntity target = getBuffer(helper);
        int machineCount = countItem(target.getInputInventory(), Items.CLAY_BALL);
        int playerCount = player.getInventory().countItem(Items.CLAY_BALL);
        if (target.getInputInventory().getSlots() != 2 || machineCount + playerCount != 148) {
            helper.fail("Downgrade changed inventory capacity incorrectly or lost overflow", MACHINE_POS);
            return;
        }
        if (player.getInventory().countItem(sourceBlock.asItem()) != 1) {
            helper.fail("The replaced buffer item was not returned", MACHINE_POS);
            return;
        }
        helper.succeed();
    }

    private static void rollsBackCanceledPlacement(GameTestHelper helper) {
        ClayContainerBlock sourceBlock = ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(1).get();
        ClayContainerBlock targetBlock = ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(4).get();
        helper.setBlock(MACHINE_POS, sourceBlock);

        WorkableClayContainerBlockEntity source = getWorkable(helper);
        source.getInputInventory().setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 7));
        source.getEnergyHolder().addEnergy(77L);
        CompoundTag before = source.saveWithoutMetadata(helper.getLevel().registryAccess());

        BlockPos absolutePos = helper.absolutePos(MACHINE_POS);
        CancelPlacementListener listener = new CancelPlacementListener(absolutePos);
        NeoForge.EVENT_BUS.register(listener);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        try {
            InteractionResult result = replace(helper, player, targetBlock);
            if (result != InteractionResult.FAIL) {
                helper.fail("Expected the canceled replacement to fail", MACHINE_POS);
                return;
            }
        } finally {
            NeoForge.EVENT_BUS.unregister(listener);
        }

        if (!helper.getBlockState(MACHINE_POS).is(sourceBlock)) {
            helper.fail("Canceled replacement did not restore the source block", MACHINE_POS);
            return;
        }
        ClayContainerBlockEntity restored = (ClayContainerBlockEntity) helper.getBlockEntity(MACHINE_POS);
        CompoundTag after = restored.saveWithoutMetadata(helper.getLevel().registryAccess());
        if (!before.equals(after)) {
            helper.fail("Canceled replacement did not restore BlockEntity data", MACHINE_POS);
            return;
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 1 ||
                player.getInventory().countItem(sourceBlock.asItem()) != 0) {
            helper.fail("Canceled replacement changed player items", MACHINE_POS);
            return;
        }
        helper.succeed();
    }

    private static void preservesCreativeItems(GameTestHelper helper) {
        ClayContainerBlock sourceBlock = ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(1).get();
        ClayContainerBlock targetBlock = ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(4).get();
        helper.setBlock(MACHINE_POS, sourceBlock);

        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;
        InteractionResult result = replace(helper, player, targetBlock);
        if (!result.consumesAction() || !helper.getBlockState(MACHINE_POS).is(targetBlock)) {
            helper.fail("Creative replacement did not install the target machine", MACHINE_POS);
            return;
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 1 ||
                player.getInventory().countItem(sourceBlock.asItem()) != 0) {
            helper.fail("Creative replacement changed player items", MACHINE_POS);
            return;
        }
        helper.succeed();
    }

    private static void rejectsSameMachine(GameTestHelper helper) {
        ClayContainerBlock block = ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(1).get();
        helper.setBlock(MACHINE_POS, block);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        InteractionResult result = replace(helper, player, block);
        if (result != InteractionResult.FAIL || !helper.getBlockState(MACHINE_POS).is(block)) {
            helper.fail("Same-machine replacement was not rejected", MACHINE_POS);
            return;
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 1 ||
                player.getInventory().countItem(block.asItem()) != 1) {
            helper.fail("Rejected same-machine replacement changed player items", MACHINE_POS);
            return;
        }
        helper.succeed();
    }

    private static void rejectsActiveRecipeDowngrade(GameTestHelper helper) {
        ClayContainerBlock sourceBlock = ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(4).get();
        ClayContainerBlock targetBlock = ClayiumBlocks.BENDING_MACHINE_BLOCKS.get(1).get();
        helper.setBlock(MACHINE_POS, sourceBlock);

        WorkableClayContainerBlockEntity source = getWorkable(helper);
        RecipeHolder<MachineRecipe> activeRecipe = helper.getLevel().getRecipeManager()
                .getAllRecipesFor(ClayiumRecipeTypes.BENDING_MACHINE_RECIPE_TYPE.get()).stream()
                .filter(holder -> holder.value().recipeTier() > targetBlock.tier)
                .filter(holder -> holder.value().recipeTier() <= source.tier)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected a bending recipe above tier 1"));
        CompoundTag state = source.saveWithoutMetadata(helper.getLevel().registryAccess());
        CompoundTag recipeState = state.getCompound(AbstractRecipeLogic.TRAIT_ID);
        recipeState.putString("recipeId", activeRecipe.id().toString());
        recipeState.putLong("processingCEPerTick", activeRecipe.value().cePerTick());
        recipeState.putLong("processingDuration", activeRecipe.value().duration());
        state.put(AbstractRecipeLogic.TRAIT_ID, recipeState);
        source.loadWithComponents(state, helper.getLevel().registryAccess());
        if (!source.getRecipeLogic().isProcessingRecipe()) {
            helper.fail("Failed to seed an active recipe", MACHINE_POS);
            return;
        }

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        InteractionResult result = replace(helper, player, targetBlock);
        if (result != InteractionResult.FAIL || !helper.getBlockState(MACHINE_POS).is(sourceBlock)) {
            helper.fail("Active-recipe downgrade was not rejected", MACHINE_POS);
            return;
        }
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 1 ||
                player.getInventory().countItem(sourceBlock.asItem()) != 0) {
            helper.fail("Rejected active-recipe downgrade changed player items", MACHINE_POS);
            return;
        }
        helper.succeed();
    }

    private static InteractionResult replace(GameTestHelper helper, Player player, Block targetBlock) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(targetBlock));
        BlockPos absolutePos = helper.absolutePos(MACHINE_POS);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        return MachineReplacementService.replace(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
    }

    private static WorkableClayContainerBlockEntity getWorkable(GameTestHelper helper) {
        if (helper.getBlockEntity(MACHINE_POS) instanceof WorkableClayContainerBlockEntity workable) {
            return workable;
        }
        throw new IllegalStateException("Expected a workable machine");
    }

    private static ClayBufferBlockEntity getBuffer(GameTestHelper helper) {
        if (helper.getBlockEntity(MACHINE_POS) instanceof ClayBufferBlockEntity buffer) {
            return buffer;
        }
        throw new IllegalStateException("Expected a clay buffer");
    }

    private static int countItem(IItemHandler handler, net.minecraft.world.item.Item item) {
        int count = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static final class CancelPlacementListener {

        private final BlockPos pos;

        private CancelPlacementListener(BlockPos pos) {
            this.pos = pos;
        }

        @SubscribeEvent
        public void onPlace(BlockEvent.EntityPlaceEvent event) {
            if (event.getPos().equals(this.pos)) {
                event.setCanceled(true);
            }
        }
    }
}
