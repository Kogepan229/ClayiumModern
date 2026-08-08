package net.kogepan.clayium.machine.replacement;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.api.machine.replacement.MachineReplacementAdapter;
import net.kogepan.clayium.api.machine.replacement.MachineReplacementApi;
import net.kogepan.clayium.api.machine.replacement.MachineReplacementContext;
import net.kogepan.clayium.api.machine.replacement.MachineReplacementDecision;
import net.kogepan.clayium.api.machine.replacement.MachineReplacementSnapshot;
import net.kogepan.clayium.api.machine.replacement.MachineReplacementSnapshot.InventorySnapshot;
import net.kogepan.clayium.api.machine.replacement.ReplacementInventoryChannel;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blocks.ClayContainerBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MachineReplacementService {

    private static final Set<ReplacementPosition> ACTIVE_REPLACEMENTS = new HashSet<>();

    private MachineReplacementService() {}

    public static InteractionResult replace(UseOnContext useContext) {
        Player player = useContext.getPlayer();
        if (player == null || !(useContext.getLevel() instanceof ServerLevel level) ||
                !(useContext.getItemInHand().getItem() instanceof BlockItem blockItem) ||
                !(blockItem.getBlock() instanceof ClayContainerBlock targetBlock)) {
            return InteractionResult.PASS;
        }

        BlockPos pos = useContext.getClickedPos();
        ReplacementPosition replacementPosition = new ReplacementPosition(level.dimension(), pos.immutable());
        if (!ACTIVE_REPLACEMENTS.add(replacementPosition)) {
            showFailure(player, Component.translatable("message.clayium.machine_replacement.busy"));
            return InteractionResult.FAIL;
        }

        try {
            return replaceLocked(level, player, useContext, targetBlock);
        } finally {
            ACTIVE_REPLACEMENTS.remove(replacementPosition);
        }
    }

    private static InteractionResult replaceLocked(ServerLevel level, Player player, UseOnContext useContext,
                                                   ClayContainerBlock targetBlock) {
        BlockPos pos = useContext.getClickedPos();
        BlockHitResult hitResult = new BlockHitResult(
                useContext.getClickLocation(), useContext.getClickedFace(), pos, useContext.isInside());
        ItemStack heldStack = useContext.getItemInHand();
        BlockState sourceState = level.getBlockState(pos);
        if (player.isSpectator() || !level.getWorldBorder().isWithinBounds(pos) ||
                !player.mayUseItemAt(pos, useContext.getClickedFace(), heldStack) ||
                !(sourceState.getBlock() instanceof ClayContainerBlock sourceBlock) ||
                !(level.getBlockEntity(pos) instanceof ClayContainerBlockEntity source)) {
            return InteractionResult.FAIL;
        }
        if (!heldStack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag().isEmpty()) {
            showFailure(player, Component.translatable("message.clayium.machine_replacement.block_entity_data"));
            return InteractionResult.FAIL;
        }

        BlockState targetState = createTargetState(sourceState, sourceBlock, targetBlock);
        ClayContainerBlockEntity stagedTarget = createTargetBlockEntity(targetBlock, pos, targetState);
        if (stagedTarget == null) {
            showFailure(player, Component.translatable("message.clayium.machine_replacement.incompatible"));
            return InteractionResult.FAIL;
        }

        MachineReplacementAdapter sourceAdapter = MachineReplacementApi.find(source.getType());
        MachineReplacementAdapter targetAdapter = MachineReplacementApi.find(stagedTarget.getType());
        if (sourceAdapter == null || targetAdapter == null ||
                !sourceAdapter.familyId().equals(targetAdapter.familyId())) {
            showFailure(player, Component.translatable("message.clayium.machine_replacement.incompatible"));
            return InteractionResult.FAIL;
        }

        MachineReplacementContext preflightContext = new MachineReplacementContext(
                level, pos, player, useContext.getHand(), hitResult, sourceState, targetState,
                source, stagedTarget);
        MachineReplacementDecision decision = sourceAdapter.canReplace(preflightContext);
        if (decision.allowed() && sourceAdapter != targetAdapter) {
            decision = targetAdapter.canReplace(preflightContext);
        }
        if (!decision.allowed()) {
            showFailure(player, decision.failureMessage());
            return InteractionResult.FAIL;
        }

        BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(level, pos, sourceState, player);
        if (NeoForge.EVENT_BUS.post(breakEvent).isCanceled()) {
            showFailure(player, Component.translatable("message.clayium.machine_replacement.canceled"));
            return InteractionResult.FAIL;
        }
        if (!level.getBlockState(pos).equals(sourceState) || level.getBlockEntity(pos) != source ||
                player.getItemInHand(useContext.getHand()) != heldStack || heldStack.isEmpty()) {
            showFailure(player, Component.translatable("message.clayium.machine_replacement.changed"));
            return InteractionResult.FAIL;
        }

        closeMenusAt(level, pos);
        BlockSnapshot blockSnapshot = BlockSnapshot.create(level.dimension(), level, pos, Block.UPDATE_ALL);
        MachineReplacementSnapshot replacementSnapshot;
        CompoundTag targetTemplate;
        try {
            replacementSnapshot = captureSnapshot(sourceAdapter, source, level);
            targetTemplate = targetAdapter.saveState(stagedTarget, level.registryAccess());
        } catch (RuntimeException exception) {
            Clayium.LOGGER.error("Failed to capture machine replacement data at {}", pos, exception);
            showFailure(player, Component.translatable("message.clayium.machine_replacement.failed"));
            return InteractionResult.FAIL;
        }

        boolean committed = false;
        try {
            source.setReplacementInventoryDropsSuppressed(true);
            if (!level.setBlock(pos, targetState, Block.UPDATE_ALL) ||
                    !(level.getBlockEntity(pos) instanceof ClayContainerBlockEntity target)) {
                throw new MachineReplacementException(
                        Component.translatable("message.clayium.machine_replacement.failed"));
            }
            MachineReplacementAdapter installedAdapter = MachineReplacementApi.find(target.getType());
            if (installedAdapter == null || !installedAdapter.familyId().equals(targetAdapter.familyId())) {
                throw new MachineReplacementException(
                        Component.translatable("message.clayium.machine_replacement.incompatible"));
            }

            CompoundTag replacementState = replacementSnapshot.state();
            normalizeInventoryHandlerSizes(replacementState, targetTemplate);
            installedAdapter.loadState(target, replacementState, level.registryAccess());
            List<ItemStack> remainders = restoreInventories(
                    replacementSnapshot.inventories(), installedAdapter.getInventoryChannels(target));

            MachineReplacementContext installedContext = new MachineReplacementContext(
                    level, pos, player, useContext.getHand(), hitResult, sourceState,
                    level.getBlockState(pos), source, target);
            MachineReplacementDecision installedDecision = installedAdapter.validateInstalled(installedContext);
            if (!installedDecision.allowed()) {
                throw new MachineReplacementException(installedDecision.failureMessage());
            }
            installedAdapter.afterReplacement(installedContext);

            BlockState finalState = target.updatePipeConnectionState(level.getBlockState(pos));
            if (!finalState.equals(level.getBlockState(pos))) {
                level.setBlock(pos, finalState, Block.UPDATE_ALL);
            }
            if (EventHooks.onBlockPlace(player, blockSnapshot, useContext.getClickedFace())) {
                throw new MachineReplacementException(
                        Component.translatable("message.clayium.machine_replacement.canceled"));
            }

            commitItemExchange(level, pos, player, useContext, sourceState, remainders);
            target.setChanged();
            BlockState synchronizedState = level.getBlockState(pos);
            level.sendBlockUpdated(pos, sourceState, synchronizedState, Block.UPDATE_ALL);
            level.levelEvent(2001, pos, Block.getId(sourceState));
            SoundType soundType = synchronizedState.getSoundType(level, pos, player);
            level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, synchronizedState));
            committed = true;
            return InteractionResult.SUCCESS;
        } catch (MachineReplacementException exception) {
            showFailure(player, exception.failureMessage);
            return InteractionResult.FAIL;
        } catch (RuntimeException exception) {
            Clayium.LOGGER.error("Machine replacement failed at {}", pos, exception);
            showFailure(player, Component.translatable("message.clayium.machine_replacement.failed"));
            return InteractionResult.FAIL;
        } finally {
            if (!committed) {
                rollback(level, pos, blockSnapshot, source);
            }
            BlockEntity current = level.getBlockEntity(pos);
            if (current == source) {
                source.setReplacementInventoryDropsSuppressed(false);
            }
        }
    }

    @Nullable
    private static ClayContainerBlockEntity createTargetBlockEntity(
                                                                    ClayContainerBlock targetBlock,
                                                                    BlockPos pos,
                                                                    BlockState targetState) {
        if (!(targetBlock instanceof EntityBlock entityBlock)) {
            return null;
        }
        BlockEntity blockEntity = entityBlock.newBlockEntity(pos, targetState);
        return blockEntity instanceof ClayContainerBlockEntity container ? container : null;
    }

    private static BlockState createTargetState(BlockState sourceState, ClayContainerBlock sourceBlock,
                                                ClayContainerBlock targetBlock) {
        BlockState targetState = targetBlock.defaultBlockState();
        DirectionProperty sourceFacingProperty = sourceBlock.getFacingProperty();
        DirectionProperty targetFacingProperty = targetBlock.getFacingProperty();
        Direction facing = sourceState.getValue(sourceFacingProperty);
        if (targetFacingProperty.getPossibleValues().contains(facing)) {
            targetState = targetState.setValue(targetFacingProperty, facing);
        }
        targetState = targetState.setValue(ClayContainerBlock.PIPE, sourceState.getValue(ClayContainerBlock.PIPE));
        for (Direction direction : Direction.values()) {
            targetState = targetState.setValue(ClayContainerBlock.getProperty(direction), false);
        }
        return targetState;
    }

    private static MachineReplacementSnapshot captureSnapshot(
                                                              MachineReplacementAdapter adapter,
                                                              ClayContainerBlockEntity source,
                                                              ServerLevel level) {
        List<InventorySnapshot> inventories = new ArrayList<>();
        Set<ResourceLocation> ids = new HashSet<>();
        Set<IItemHandler> handlers = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ReplacementInventoryChannel channel : adapter.getInventoryChannels(source)) {
            if (!ids.add(channel.id()) || !handlers.add(channel.handler())) {
                throw new IllegalStateException("Duplicate machine replacement inventory channel " + channel.id());
            }
            List<ItemStack> stacks = new ArrayList<>(channel.handler().getSlots());
            for (int slot = 0; slot < channel.handler().getSlots(); slot++) {
                stacks.add(channel.handler().getStackInSlot(slot).copy());
            }
            inventories.add(new InventorySnapshot(channel.id(), stacks));
        }
        return new MachineReplacementSnapshot(adapter.saveState(source, level.registryAccess()), inventories);
    }

    private static List<ItemStack> restoreInventories(
                                                      List<InventorySnapshot> sourceInventories,
                                                      List<ReplacementInventoryChannel> targetChannels) {
        Map<ResourceLocation, IItemHandler> targets = new LinkedHashMap<>();
        Set<IItemHandler> handlers = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ReplacementInventoryChannel channel : targetChannels) {
            if (targets.putIfAbsent(channel.id(), channel.handler()) != null || !handlers.add(channel.handler())) {
                throw new IllegalStateException("Duplicate machine replacement inventory channel " + channel.id());
            }
            clearHandler(channel.handler());
        }

        List<ItemStack> remainders = new ArrayList<>();
        for (InventorySnapshot sourceInventory : sourceInventories) {
            IItemHandler target = targets.get(sourceInventory.id());
            for (int slot = 0; slot < sourceInventory.stacks().size(); slot++) {
                ItemStack stack = sourceInventory.stacks().get(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                if (target == null) {
                    remainders.add(stack.copy());
                    continue;
                }
                ItemStack remainder = slot < target.getSlots() ? target.insertItem(slot, stack.copy(), false) :
                        stack.copy();
                if (!remainder.isEmpty()) {
                    remainder = ItemHandlerHelper.insertItemStacked(target, remainder, false);
                }
                if (!remainder.isEmpty()) {
                    remainders.add(remainder.copy());
                }
            }
        }
        return remainders;
    }

    private static void clearHandler(IItemHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            while (!handler.getStackInSlot(slot).isEmpty()) {
                int previousCount = handler.getStackInSlot(slot).getCount();
                ItemStack extracted = handler.extractItem(slot, Integer.MAX_VALUE, false);
                if (extracted.isEmpty() || handler.getStackInSlot(slot).getCount() >= previousCount) {
                    throw new IllegalStateException("Replacement inventory channel cannot be cleared");
                }
            }
        }
    }

    private static void normalizeInventoryHandlerSizes(CompoundTag source, CompoundTag targetTemplate) {
        for (String key : List.copyOf(source.getAllKeys())) {
            Tag sourceValue = source.get(key);
            Tag targetValue = targetTemplate.get(key);
            if (!(sourceValue instanceof CompoundTag sourceCompound)) {
                continue;
            }
            if (isItemHandlerTag(sourceCompound)) {
                if (!(targetValue instanceof CompoundTag targetCompound) || !isItemHandlerTag(targetCompound)) {
                    source.remove(key);
                    continue;
                }
                int targetSize = Math.max(0, targetCompound.getInt("Size"));
                sourceCompound.putInt("Size", targetSize);
                ListTag filteredItems = new ListTag();
                for (Tag item : sourceCompound.getList("Items", Tag.TAG_COMPOUND)) {
                    if (item instanceof CompoundTag itemCompound) {
                        int slot = itemCompound.getInt("Slot");
                        if (slot >= 0 && slot < targetSize) {
                            filteredItems.add(itemCompound.copy());
                        }
                    }
                }
                sourceCompound.put("Items", filteredItems);
            } else if (targetValue instanceof CompoundTag targetCompound) {
                normalizeInventoryHandlerSizes(sourceCompound, targetCompound);
            }
        }
    }

    private static boolean isItemHandlerTag(CompoundTag tag) {
        return tag.contains("Size", Tag.TAG_INT) && tag.contains("Items", Tag.TAG_LIST);
    }

    private static void closeMenusAt(ServerLevel level, BlockPos pos) {
        for (ServerPlayer serverPlayer : List.copyOf(level.players())) {
            if (serverPlayer.containerMenu instanceof ModularUIContainerMenu menu &&
                    menu.uiHolder instanceof BlockUIMenuType.BlockUIHolder holder && holder.pos.equals(pos)) {
                serverPlayer.closeContainer();
            }
        }
    }

    private static void commitItemExchange(ServerLevel level, BlockPos pos, Player player,
                                           UseOnContext useContext, BlockState sourceState,
                                           List<ItemStack> remainders) {
        if (!player.getAbilities().instabuild) {
            useContext.getItemInHand().shrink(1);
            ItemStack oldMachine = sourceState.getBlock().asItem().getDefaultInstance();
            if (!oldMachine.is(Items.AIR)) {
                giveOrDrop(level, pos, player, oldMachine);
            }
        }
        for (ItemStack remainder : remainders) {
            giveOrDrop(level, pos, player, remainder);
        }
    }

    private static void giveOrDrop(ServerLevel level, BlockPos pos, Player player, ItemStack stack) {
        ItemStack remainder = stack.copy();
        player.addItem(remainder);
        if (!remainder.isEmpty()) {
            Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, remainder);
        }
    }

    private static void rollback(ServerLevel level, BlockPos pos, BlockSnapshot snapshot,
                                 ClayContainerBlockEntity source) {
        BlockEntity currentBeforeRollback = level.getBlockEntity(pos);
        if (currentBeforeRollback instanceof ClayContainerBlockEntity currentContainer && currentContainer != source) {
            currentContainer.setReplacementInventoryDropsSuppressed(true);
        }
        if (!snapshot.restore(Block.UPDATE_ALL)) {
            Clayium.LOGGER.error("Failed to restore machine replacement snapshot at {}", pos);
            level.setBlock(pos, snapshot.getState(), Block.UPDATE_ALL);
            snapshot.restoreBlockEntity(level, pos);
        }
        if (level.getBlockEntity(pos) == source) {
            source.setReplacementInventoryDropsSuppressed(false);
        }
        BlockState restoredState = level.getBlockState(pos);
        level.sendBlockUpdated(pos, restoredState, restoredState, Block.UPDATE_ALL);
    }

    private static void showFailure(Player player, @Nullable Component message) {
        if (message != null) {
            player.displayClientMessage(message, true);
        }
    }

    private record ReplacementPosition(ResourceKey<Level> dimension, BlockPos pos) {}

    private static final class MachineReplacementException extends RuntimeException {

        private final Component failureMessage;

        private MachineReplacementException(Component failureMessage) {
            this.failureMessage = failureMessage;
        }
    }
}
