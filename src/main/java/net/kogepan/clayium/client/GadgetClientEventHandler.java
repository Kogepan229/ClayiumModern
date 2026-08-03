package net.kogepan.clayium.client;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.api.gadget.ClayGadgetApi;
import net.kogepan.clayium.api.gadget.ClayGadgetCategories;
import net.kogepan.clayium.api.gadget.ClayGadgetEvents;
import net.kogepan.clayium.items.gadget.behavior.FlightGadget;
import net.kogepan.clayium.items.gadget.behavior.OverclockGadget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Clayium.MODID, value = Dist.CLIENT)
public final class GadgetClientEventHandler {

    private static final float FREE_FLIGHT_VELOCITY = 0.7F;
    private static final float SUPERSONIC_ACCELERATION = 0.9F;
    private static final float SUPERSONIC_DIVISION = 1.1F;

    private GadgetClientEventHandler() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (event.getEntity() != minecraft.player) {
            return;
        }
        ClayGadgetApi.findActive(event.getEntity(), ClayGadgetCategories.OVERCLOCK)
                .filter(active -> active.gadget() instanceof OverclockGadget)
                .ifPresent(active -> applyOverclock(minecraft, ((OverclockGadget) active.gadget()).delay()));
        ClayGadgetApi.findActive(event.getEntity(), ClayGadgetCategories.FLIGHT)
                .filter(active -> active.gadget() instanceof FlightGadget)
                .ifPresent(active -> applyFlight((LocalPlayer) event.getEntity(),
                        ((FlightGadget) active.gadget()).mode(), minecraft));
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (event.getPlayer() != null) {
            ClayGadgetEvents.deactivateForLifecycle(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onClientClone(ClientPlayerNetworkEvent.Clone event) {
        ClayGadgetEvents.deactivateForLifecycle(event.getOldPlayer());
        ClayGadgetEvents.deactivateForLifecycle(event.getNewPlayer());
    }

    private static void applyOverclock(Minecraft minecraft, int delay) {
        MultiPlayerGameMode gameMode = minecraft.gameMode;
        if (gameMode != null && gameMode.destroyDelay > delay) {
            gameMode.destroyDelay = delay;
        }
        if (minecraft.rightClickDelay > delay) {
            minecraft.rightClickDelay = delay;
        }
    }

    private static void applyFlight(LocalPlayer player, int mode, Minecraft minecraft) {
        if (mode < 1 || !player.getAbilities().flying) {
            return;
        }
        Vec3 movement = player.getDeltaMovement();
        float sin = Mth.sin(player.getYRot() * Mth.DEG_TO_RAD);
        float cos = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD);
        float forward = (float) (movement.z * cos - movement.x * sin);
        float strafe = (float) (movement.z * sin + movement.x * cos);
        double vertical = movement.y;

        if (player.input.shiftKeyDown) {
            vertical = mode >= 2 ? (vertical - SUPERSONIC_ACCELERATION) / SUPERSONIC_DIVISION : -FREE_FLIGHT_VELOCITY;
        }
        if (player.input.jumping) {
            vertical = mode >= 2 ? (vertical + SUPERSONIC_ACCELERATION) / SUPERSONIC_DIVISION : FREE_FLIGHT_VELOCITY;
        }
        if (player.input.jumping == player.input.shiftKeyDown) {
            vertical = 0.0D;
        }

        if (minecraft.options.keyUp.isDown()) {
            forward = mode >= 2 ? (forward + SUPERSONIC_ACCELERATION) / SUPERSONIC_DIVISION : FREE_FLIGHT_VELOCITY;
        }
        if (minecraft.options.keyDown.isDown()) {
            forward = mode >= 2 ? (forward - SUPERSONIC_ACCELERATION) / SUPERSONIC_DIVISION : -FREE_FLIGHT_VELOCITY;
        }
        if (!minecraft.options.keyUp.isDown() && !minecraft.options.keyDown.isDown()) {
            forward = 0.0F;
        }
        if (minecraft.options.keyLeft.isDown()) {
            strafe = mode >= 2 ? (strafe + SUPERSONIC_ACCELERATION) / SUPERSONIC_DIVISION : FREE_FLIGHT_VELOCITY;
        }
        if (minecraft.options.keyRight.isDown()) {
            strafe = mode >= 2 ? (strafe - SUPERSONIC_ACCELERATION) / SUPERSONIC_DIVISION : -FREE_FLIGHT_VELOCITY;
        }
        if (!minecraft.options.keyLeft.isDown() && !minecraft.options.keyRight.isDown()) {
            strafe = 0.0F;
        }

        player.setDeltaMovement(strafe * cos - forward * sin, vertical,
                forward * cos + strafe * sin);
    }
}
