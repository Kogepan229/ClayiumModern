package net.kogepan.clayium.items.tools;

import net.kogepan.clayium.Config;
import net.kogepan.clayium.registries.ClayiumDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public final class ClaySteelToolMining {

    private static final Map<Player, MiningTarget> MINING_TARGETS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<MinecraftServer, List<PendingMining>> PENDING_MINING = Collections
            .synchronizedMap(new WeakHashMap<>());
    private static final ThreadLocal<Boolean> CALCULATING_SPEED = ThreadLocal.withInitial(() -> false);

    private ClaySteelToolMining() {}

    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (!(event.getItemStack().getItem() instanceof ClaySteelTool)) {
            MINING_TARGETS.remove(player);
            return;
        }
        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.ABORT) {
            MINING_TARGETS.remove(player);
            return;
        }
        Direction face = event.getFace();
        if (face != null) {
            MINING_TARGETS.put(player, new MiningTarget(event.getPos().immutable(), face));
        }
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (CALCULATING_SPEED.get() || !(event.getEntity().getMainHandItem().getItem() instanceof ClaySteelTool)) {
            return;
        }
        BlockPos origin = event.getPosition().orElse(null);
        MiningTarget target = MINING_TARGETS.get(event.getEntity());
        if (origin == null || target == null || !target.pos().equals(origin)) {
            return;
        }

        Level level = event.getEntity().level();
        Basis basis = Basis.create(target.face(), event.getEntity().getDirection());
        List<BlockPos> selected = getSelectedPositions(event.getEntity().getMainHandItem(), origin, basis);
        if (selected.size() <= 1) {
            return;
        }

        float primaryTicks = 0.0F;
        float totalTicks = 0.0F;
        CALCULATING_SPEED.set(true);
        try {
            for (BlockPos selectedPos : selected) {
                BlockState selectedState = level.getBlockState(selectedPos);
                if (selectedState.isAir() || !level.mayInteract(event.getEntity(), selectedPos)) {
                    continue;
                }
                float progress = selectedState.getDestroyProgress(event.getEntity(), level, selectedPos);
                if (progress <= 0.0F) {
                    event.setNewSpeed(0.0F);
                    return;
                }
                float ticks = 1.0F / progress;
                totalTicks += ticks;
                if (selectedPos.equals(origin)) {
                    primaryTicks = ticks;
                }
            }
        } finally {
            CALCULATING_SPEED.remove();
        }

        if (primaryTicks > 0.0F && totalTicks > primaryTicks && Float.isFinite(totalTicks)) {
            event.setNewSpeed(event.getNewSpeed() * primaryTicks / totalTicks);
        }
    }

    public static InteractionResult configure(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        int maxRange = Config.CLAY_STEEL_TOOL_RANGE.get();
        ClaySteelToolData data = stack.getOrDefault(
                ClayiumDataComponents.CLAY_STEEL_TOOL_DATA.get(),
                ClaySteelToolData.EMPTY);
        int mode = Math.min(data.mode(), maxRange);
        if (player.isSecondaryUseActive()) {
            return mode == maxRange && !Config.UTILITY_MODE.get() ? placeClayBlock(context, player) :
                    InteractionResult.PASS;
        }
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (mode == maxRange && context.getLevel().getBlockState(context.getClickedPos()).is(Blocks.CLAY)) {
            List<BlockPos> offsets = captureCustomOffsets(
                    context.getLevel(),
                    context.getClickedPos(),
                    Basis.create(context.getClickedFace(), player.getDirection()),
                    maxRange);
            stack.set(
                    ClayiumDataComponents.CLAY_STEEL_TOOL_DATA.get(),
                    data.withMode(mode).withCustomOffsets(offsets));
            player.sendOverlayMessage(
                    Component.translatable("message.clayium.clay_steel_tool.customized", offsets.size()));
        } else {
            int newMode = mode >= maxRange ? 0 : mode + 1;
            stack.set(ClayiumDataComponents.CLAY_STEEL_TOOL_DATA.get(), data.withMode(newMode));
            player.sendOverlayMessage(
                    Component.translatable("message.clayium.clay_steel_tool.mode", newMode));
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    private static InteractionResult placeClayBlock(UseOnContext context, Player player) {
        ItemStack clayStack = new ItemStack(Blocks.CLAY, 2);
        BlockHitResult hitResult = new BlockHitResult(
                context.getClickLocation(),
                context.getClickedFace(),
                context.getClickedPos(),
                context.isInside());
        // The held stack's useOn wrapper already captures snapshots and fires the server-side placement event.
        return clayStack.getItem().useOn(new UseOnContext(
                context.getLevel(),
                player,
                context.getHand(),
                clayStack,
                hitResult));
    }

    static void appendTooltip(ItemStack stack, String tooltipKey, Consumer<Component> builder) {
        int maxRange = Config.CLAY_STEEL_TOOL_RANGE.get();
        ClaySteelToolData data = stack.getOrDefault(
                ClayiumDataComponents.CLAY_STEEL_TOOL_DATA.get(),
                ClaySteelToolData.EMPTY);
        int mode = Math.min(data.mode(), maxRange);
        builder.accept(Component.translatable(tooltipKey));
        builder.accept(Component.translatable("item.clayium.clay_steel_tool.mode", mode));
        if (data.mode() == maxRange && !data.customOffsets().isEmpty()) {
            builder.accept(Component.translatable(
                    "item.clayium.clay_steel_tool.area.custom",
                    data.customOffsets().size()));
        } else {
            int width = mode * 2 + 1;
            builder.accept(Component.translatable(
                    "item.clayium.clay_steel_tool.area.square",
                    width,
                    width));
        }
        builder.accept(Component.translatable("item.clayium.clay_steel_tool.configure"));
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        List<PendingMining> pending;
        synchronized (PENDING_MINING) {
            pending = PENDING_MINING.remove(event.getServer());
        }
        if (pending == null) {
            return;
        }
        for (PendingMining request : pending) {
            mineAdditionalBlocks(request);
        }
    }

    static void queueAdditionalBlocks(ItemStack stack, ServerLevel level, BlockPos origin, ServerPlayer player) {
        MiningTarget target = MINING_TARGETS.remove(player);
        if (target == null || !target.pos().equals(origin)) {
            return;
        }

        List<BlockPos> selected = getSelectedPositions(
                stack,
                origin,
                Basis.create(target.face(), player.getDirection()));
        synchronized (PENDING_MINING) {
            PENDING_MINING.computeIfAbsent(level.getServer(), ignored -> new ArrayList<>())
                    .add(new PendingMining(stack, level, origin.immutable(), player, selected));
        }
    }

    private static void mineAdditionalBlocks(PendingMining request) {
        if (request.player().level() != request.level() || request.player().getMainHandItem() != request.stack()) {
            return;
        }
        for (BlockPos selectedPos : request.selected()) {
            if (request.stack().isEmpty()) {
                break;
            }
            if (selectedPos.equals(request.origin())) {
                continue;
            }
            BlockState selectedState = request.level().getBlockState(selectedPos);
            if (selectedState.isAir() || selectedState.getDestroySpeed(request.level(), selectedPos) < 0.0F ||
                    !request.level().mayInteract(request.player(), selectedPos)) {
                continue;
            }
            request.player().gameMode.destroyBlock(selectedPos);
        }
    }

    private static List<BlockPos> getSelectedPositions(ItemStack stack, BlockPos origin, Basis basis) {
        int maxRange = Config.CLAY_STEEL_TOOL_RANGE.get();
        ClaySteelToolData data = stack.getOrDefault(
                ClayiumDataComponents.CLAY_STEEL_TOOL_DATA.get(),
                ClaySteelToolData.EMPTY);
        int mode = Math.min(data.mode(), maxRange);
        if (data.mode() == maxRange && !data.customOffsets().isEmpty()) {
            return data.customOffsets().stream()
                    .map(offset -> basis.apply(origin, offset.getX(), offset.getY(), offset.getZ()))
                    .toList();
        }

        List<BlockPos> selected = new ArrayList<>((mode * 2 + 1) * (mode * 2 + 1));
        for (int localY = -mode; localY <= mode; localY++) {
            for (int localX = -mode; localX <= mode; localX++) {
                selected.add(basis.apply(origin, localX, localY, 0));
            }
        }
        return selected;
    }

    private static List<BlockPos> captureCustomOffsets(Level level, BlockPos origin, Basis basis, int range) {
        List<BlockPos> offsets = new ArrayList<>();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos selectedPos = origin.offset(x, y, z);
                    if (level.getBlockState(selectedPos).is(Blocks.CLAY)) {
                        offsets.add(basis.toLocal(x, y, z));
                    }
                }
            }
        }
        return offsets;
    }

    private record MiningTarget(BlockPos pos, Direction face) {}

    private record PendingMining(
                                 ItemStack stack,
                                 ServerLevel level,
                                 BlockPos origin,
                                 ServerPlayer player,
                                 List<BlockPos> selected) {}

    private record Basis(Direction xAxis, Direction yAxis, Direction zAxis) {

        static Basis create(Direction face, Direction playerDirection) {
            if (face.getAxis().isVertical()) {
                Direction yAxis = face == Direction.DOWN ? playerDirection.getOpposite() : playerDirection;
                return new Basis(playerDirection.getClockWise(), yAxis, face);
            }
            return new Basis(face.getCounterClockWise(), Direction.UP, face);
        }

        BlockPos apply(BlockPos origin, int localX, int localY, int localZ) {
            return origin.offset(
                    this.xAxis.getStepX() * localX + this.yAxis.getStepX() * localY + this.zAxis.getStepX() * localZ,
                    this.xAxis.getStepY() * localX + this.yAxis.getStepY() * localY + this.zAxis.getStepY() * localZ,
                    this.xAxis.getStepZ() * localX + this.yAxis.getStepZ() * localY + this.zAxis.getStepZ() * localZ);
        }

        BlockPos toLocal(int worldX, int worldY, int worldZ) {
            return new BlockPos(
                    this.xAxis.getStepX() * worldX + this.xAxis.getStepY() * worldY + this.xAxis.getStepZ() * worldZ,
                    this.yAxis.getStepX() * worldX + this.yAxis.getStepY() * worldY + this.yAxis.getStepZ() * worldZ,
                    this.zAxis.getStepX() * worldX + this.zAxis.getStepY() * worldY + this.zAxis.getStepZ() * worldZ);
        }
    }
}
