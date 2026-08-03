package net.kogepan.clayium.api.gadget;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import org.jetbrains.annotations.ApiStatus;

@EventBusSubscriber(modid = Clayium.MODID)
public final class ClayGadgetEvents {

    private ClayGadgetEvents() {}

    /** Internal bridge for client-only lifecycle events. */
    @ApiStatus.Internal
    public static void deactivateForLifecycle(Player player) {
        ClayGadgetManager.deactivateAll(player);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        ClayGadgetManager.tick(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ClayGadgetManager.deactivateAll(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        ClayGadgetManager.deactivateAll(event.getOriginal());
        ClayGadgetManager.deactivateAll(event.getEntity());
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Player player &&
                ClayGadgetApi.isActive(player, ClayiumItems.GADGET_REPEATEDLY_ATTACK)) {
            event.getEntity().invulnerableTime = 0;
            event.setInvulnerabilityTicks(0);
        }
    }
}
