package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.client.ldlib.elements.MachineControlButton;
import net.kogepan.clayium.client.ldlib.textures.ButtonTextures;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.BindableValue;
import com.mojang.authlib.GameProfile;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ActivatorBlockEntity extends AdjacentWorkerBlockEntity {

    private static final int PROGRESS_PER_ACTIVATION = 400;
    private static final double RAYTRACE_DISTANCE = 3.0D;
    private static final GameProfile FAKE_PLAYER_PROFILE = new GameProfile(
            UUID.fromString("e39f82e9-2d6a-45b6-bbe1-a302509eaa17"), "[ClayActivator]");

    private TargetMode targetMode = TargetMode.BLOCK;
    private boolean raytraceEnabled;
    private boolean sneak;
    private boolean nextFixedBothTargetsBlock = true;
    private int fixedEntityCursor;

    public ActivatorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.ACTIVATOR_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE, MachineIOMode.ALL),
                List.of(MachineIOMode.NONE, MachineIOMode.ALL));
        this.addTrait(new AutoIOTrait.Combined(this, this.tier, false, 2, 2, 16, 16));
    }

    @Override
    public void initDefaultRoutes() {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof ClayContainerBlock containerBlock)) {
            return;
        }
        Direction front = state.getValue(containerBlock.getFacingProperty());
        this.inputModes.setMode(front, MachineIOMode.ALL);
        for (Direction direction : Direction.values()) {
            if (direction != front && direction != front.getOpposite()) {
                this.outputModes.setMode(direction, MachineIOMode.ALL);
            }
        }
    }

    @Override
    protected void tickWorker(@NotNull ServerLevel level) {
        this.progress += PROGRESS_PER_TICK;
        this.setChanged();
        if (this.progress < PROGRESS_PER_ACTIVATION) {
            return;
        }
        this.performActivation(level);
        this.completeJob();
    }

    private void performActivation(@NotNull ServerLevel level) {
        FakePlayer fakePlayer = FakePlayerFactory.get(level, FAKE_PLAYER_PROFILE);
        this.recoverFakePlayerInventory(level, fakePlayer);

        int sourceSlot = this.findFirstOccupiedSlot();
        ItemStack heldStack = sourceSlot >= 0 ?
                this.itemInventory.extractItem(sourceSlot, Integer.MAX_VALUE, false) : ItemStack.EMPTY;
        this.initializeFakePlayer(fakePlayer, heldStack);
        try {
            if (this.raytraceEnabled) {
                this.activateRaytraceTarget(level, fakePlayer);
            } else {
                this.activateFixedTarget(level, fakePlayer);
            }
        } finally {
            fakePlayer.stopUsingItem();
            fakePlayer.setShiftKeyDown(false);
            this.recoverFakePlayerInventory(level, fakePlayer);
        }
    }

    private int findFirstOccupiedSlot() {
        for (int slot = 0; slot < this.itemInventory.getSlots(); slot++) {
            if (!this.itemInventory.getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    private void initializeFakePlayer(@NotNull FakePlayer fakePlayer, @NotNull ItemStack heldStack) {
        fakePlayer.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
        fakePlayer.getInventory().selected = 0;
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, heldStack);
        fakePlayer.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        fakePlayer.setShiftKeyDown(this.sneak);
        fakePlayer.getCooldowns().removeCooldown(heldStack.getItem());
        fakePlayer.setDeltaMovement(Vec3.ZERO);

        Direction direction = this.getWorkDirection();
        Vec3 eyePosition = this.getRaytraceStart(direction);
        fakePlayer.setPos(eyePosition.x, eyePosition.y - fakePlayer.getEyeHeight(), eyePosition.z);
        double horizontal = Math.sqrt(direction.getStepX() * direction.getStepX() +
                direction.getStepZ() * direction.getStepZ());
        fakePlayer.setYRot((float) Math.toDegrees(Math.atan2(-direction.getStepX(), direction.getStepZ())));
        fakePlayer.setXRot((float) Math.toDegrees(Math.atan2(-direction.getStepY(), horizontal)));
        fakePlayer.setYHeadRot(fakePlayer.getYRot());
    }

    private void activateFixedTarget(@NotNull ServerLevel level, @NotNull FakePlayer fakePlayer) {
        switch (this.targetMode) {
            case BLOCK -> this.activateFixedBlock(level, fakePlayer);
            case ENTITY -> this.activateNextFixedEntity(level, fakePlayer);
            case BOTH -> {
                if (this.nextFixedBothTargetsBlock) {
                    this.activateFixedBlock(level, fakePlayer);
                } else {
                    this.activateNextFixedEntity(level, fakePlayer);
                }
                this.nextFixedBothTargetsBlock = !this.nextFixedBothTargetsBlock;
            }
        }
    }

    private void activateFixedBlock(@NotNull ServerLevel level, @NotNull FakePlayer fakePlayer) {
        BlockPos targetPos = this.getWorkPos();
        if (!this.matchesBlockFilter(level.getBlockState(targetPos))) {
            return;
        }
        Direction workDirection = this.getWorkDirection();
        Vec3 hitLocation = Vec3.atCenterOf(targetPos).subtract(
                workDirection.getStepX() * 0.5D,
                workDirection.getStepY() * 0.5D,
                workDirection.getStepZ() * 0.5D);
        this.useOnBlock(level, fakePlayer,
                new BlockHitResult(hitLocation, workDirection.getOpposite(), targetPos, false));
    }

    private void activateNextFixedEntity(@NotNull ServerLevel level, @NotNull FakePlayer fakePlayer) {
        List<Entity> entities = level.getEntities(fakePlayer, new AABB(this.getWorkPos()),
                entity -> entity.isAlive() && entity != fakePlayer);
        if (entities.isEmpty()) {
            this.fixedEntityCursor = 0;
            return;
        }
        entities.sort(Comparator.comparingInt(Entity::getId));
        int index = Math.floorMod(this.fixedEntityCursor, entities.size());
        this.fixedEntityCursor = (index + 1) % entities.size();
        this.useOnEntity(level, fakePlayer, entities.get(index));
    }

    private void activateRaytraceTarget(@NotNull ServerLevel level, @NotNull FakePlayer fakePlayer) {
        Direction direction = this.getWorkDirection();
        Vec3 start = this.getRaytraceStart(direction);
        Vec3 end = start.add(
                direction.getStepX() * RAYTRACE_DISTANCE,
                direction.getStepY() * RAYTRACE_DISTANCE,
                direction.getStepZ() * RAYTRACE_DISTANCE);

        BlockHitResult blockHit = null;
        EntityHitResult entityHit = null;
        if (this.targetMode != TargetMode.ENTITY) {
            BlockHitResult candidate = level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE, fakePlayer));
            if (candidate.getType() == HitResult.Type.BLOCK) {
                blockHit = candidate;
            }
        }
        if (this.targetMode != TargetMode.BLOCK) {
            entityHit = this.findEntityHit(level, fakePlayer, start, end);
        }

        HitResult selectedHit = blockHit;
        if (selectedHit == null || entityHit != null &&
                start.distanceToSqr(entityHit.getLocation()) < start.distanceToSqr(selectedHit.getLocation())) {
            selectedHit = entityHit;
        }
        if (selectedHit instanceof BlockHitResult selectedBlockHit) {
            if (this.matchesBlockFilter(level.getBlockState(selectedBlockHit.getBlockPos()))) {
                this.useOnBlock(level, fakePlayer, selectedBlockHit);
            }
        } else if (selectedHit instanceof EntityHitResult selectedEntityHit) {
            this.useOnEntity(level, fakePlayer, selectedEntityHit);
        } else {
            this.useItem(level, fakePlayer);
        }
    }

    @Nullable
    private EntityHitResult findEntityHit(@NotNull ServerLevel level, @NotNull FakePlayer fakePlayer,
                                          @NotNull Vec3 start, @NotNull Vec3 end) {
        Entity selected = null;
        Vec3 selectedLocation = null;
        double selectedDistance = Double.MAX_VALUE;
        AABB searchBox = new AABB(start, end).inflate(1.0D);
        for (Entity entity : level.getEntities(fakePlayer, searchBox,
                candidate -> candidate.isAlive() && candidate.isPickable() && candidate != fakePlayer)) {
            AABB hitBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            Optional<Vec3> clipped = hitBox.contains(start) ? Optional.of(start) : hitBox.clip(start, end);
            if (clipped.isEmpty()) {
                continue;
            }
            double distance = start.distanceToSqr(clipped.get());
            if (distance < selectedDistance) {
                selected = entity;
                selectedLocation = clipped.get();
                selectedDistance = distance;
            }
        }
        return selected == null ? null : new EntityHitResult(selected, selectedLocation);
    }

    private void useOnBlock(@NotNull ServerLevel level, @NotNull FakePlayer fakePlayer,
                            @NotNull BlockHitResult hitResult) {
        ItemStack stack = fakePlayer.getItemInHand(InteractionHand.MAIN_HAND);
        InteractionResult result = fakePlayer.gameMode.useItemOn(fakePlayer, level, stack,
                InteractionHand.MAIN_HAND, hitResult);
        if (result == InteractionResult.PASS) {
            this.useItem(level, fakePlayer);
        }
    }

    private void useOnEntity(@NotNull ServerLevel level, @NotNull FakePlayer fakePlayer, @NotNull Entity entity) {
        InteractionResult result = fakePlayer.interactOn(entity, InteractionHand.MAIN_HAND);
        if (!result.consumesAction()) {
            this.useItem(level, fakePlayer);
        }
    }

    private void useOnEntity(@NotNull ServerLevel level, @NotNull FakePlayer fakePlayer,
                             @NotNull EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        InteractionResult result = CommonHooks.onInteractEntityAt(fakePlayer, entity, hitResult,
                InteractionHand.MAIN_HAND);
        if (result == null) {
            Vec3 localHit = hitResult.getLocation().subtract(entity.position());
            result = entity.interactAt(fakePlayer, localHit, InteractionHand.MAIN_HAND);
        }
        if (!result.consumesAction()) {
            this.useOnEntity(level, fakePlayer, entity);
        }
    }

    private void useItem(@NotNull ServerLevel level, @NotNull FakePlayer fakePlayer) {
        ItemStack stack = fakePlayer.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.isEmpty()) {
            fakePlayer.gameMode.useItem(fakePlayer, level, stack, InteractionHand.MAIN_HAND);
        }
    }

    @NotNull
    private Vec3 getRaytraceStart(@NotNull Direction direction) {
        return Vec3.atCenterOf(this.worldPosition).add(
                direction.getStepX() * 0.501D,
                direction.getStepY() * 0.501D,
                direction.getStepZ() * 0.501D);
    }

    private void recoverFakePlayerInventory(@NotNull ServerLevel level, @NotNull FakePlayer fakePlayer) {
        Inventory inventory = fakePlayer.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            this.returnOrDrop(level, inventory.removeItemNoUpdate(slot));
        }
        AbstractContainerMenu menu = fakePlayer.containerMenu;
        this.returnOrDrop(level, menu.getCarried());
        menu.setCarried(ItemStack.EMPTY);
        inventory.selected = 0;
    }

    private void returnOrDrop(@NotNull ServerLevel level, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(this.itemInventory, stack, false);
        if (!remainder.isEmpty()) {
            Containers.dropItemStack(level, this.worldPosition.getX() + 0.5D,
                    this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D, remainder);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putByte("targetMode", (byte) this.targetMode.ordinal());
        tag.putBoolean("raytraceEnabled", this.raytraceEnabled);
        tag.putBoolean("sneak", this.sneak);
        tag.putBoolean("nextFixedBothTargetsBlock", this.nextFixedBothTargetsBlock);
        tag.putInt("fixedEntityCursor", this.fixedEntityCursor);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("targetMode")) {
            this.targetMode = TargetMode.byOrdinal(tag.getByte("targetMode"));
        }
        this.raytraceEnabled = tag.getBoolean("raytraceEnabled");
        this.sneak = tag.getBoolean("sneak");
        if (tag.contains("nextFixedBothTargetsBlock")) {
            this.nextFixedBothTargetsBlock = tag.getBoolean("nextFixedBothTargetsBlock");
        }
        this.fixedEntityCursor = tag.getInt("fixedEntityCursor");
    }

    @Override
    protected void createWorkerSettingsUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        root.addChild(new BindableValue<Integer>().bind(DataBindingBuilder.intValS2C(() -> this.targetMode.ordinal())
                .remoteSetter(value -> this.targetMode = TargetMode.byOrdinal(value)).build()));
        root.addChild(new BindableValue<Boolean>().bind(DataBindingBuilder.boolS2C(() -> this.raytraceEnabled)
                .remoteSetter(value -> this.raytraceEnabled = value).build()));
        root.addChild(new BindableValue<Boolean>().bind(DataBindingBuilder.boolS2C(() -> this.sneak)
                .remoteSetter(value -> this.sneak = value).build()));

        UIElement settings = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.ROW)
                .justifyContent(AlignContent.CENTER)
                .gapAll(2));
        settings.addChild(this.createTargetModeButton(ButtonTextures.TARGET_BLOCK, TargetMode.BLOCK,
                "gui.clayium.activator.target_block"));
        settings.addChild(this.createTargetModeButton(ButtonTextures.TARGET_ENTITY, TargetMode.ENTITY,
                "gui.clayium.activator.target_entity"));
        settings.addChild(this.createTargetModeButton(ButtonTextures.TARGET_BOTH, TargetMode.BOTH,
                "gui.clayium.activator.target_both"));
        settings.addChild(this.createBooleanButton(ButtonTextures.FIXED_TARGET, () -> this.raytraceEnabled,
                "gui.clayium.activator.fixed", () -> this.raytraceEnabled = false));
        settings.addChild(this.createBooleanButton(ButtonTextures.RAYTRACE, () -> !this.raytraceEnabled,
                "gui.clayium.activator.raytrace", () -> this.raytraceEnabled = true));
        settings.addChild(this.createBooleanButton(ButtonTextures.SNEAK_OFF, () -> this.sneak,
                "gui.clayium.activator.sneak_off", () -> this.sneak = false));
        settings.addChild(this.createBooleanButton(ButtonTextures.SNEAK_ON, () -> !this.sneak,
                "gui.clayium.activator.sneak_on", () -> this.sneak = true));
        root.addChild(settings);
    }

    private MachineControlButton createTargetModeButton(ButtonTextures.ButtonTexture texture, TargetMode mode,
                                                        String tooltipKey) {
        return this.createSelectionButton(texture, () -> this.targetMode != mode, tooltipKey,
                () -> this.updateSetting(() -> this.targetMode = mode));
    }

    private MachineControlButton createBooleanButton(ButtonTextures.ButtonTexture texture,
                                                     java.util.function.BooleanSupplier enabled,
                                                     String tooltipKey,
                                                     Runnable update) {
        return this.createSelectionButton(texture, enabled, tooltipKey, () -> this.updateSetting(update));
    }

    private void updateSetting(@NotNull Runnable update) {
        update.run();
        this.setChanged();
    }

    private enum TargetMode {

        BLOCK,
        ENTITY,
        BOTH;

        @NotNull
        private static TargetMode byOrdinal(int ordinal) {
            TargetMode[] values = values();
            return values[Math.floorMod(ordinal, values.length)];
        }
    }
}
