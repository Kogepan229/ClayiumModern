package net.kogepan.clayium.items.gadget.behavior;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.api.gadget.ClayGadgetCategories;
import net.kogepan.clayium.api.gadget.ClayGadgetContext;
import net.kogepan.clayium.api.gadget.IClayGadget;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;

import org.jetbrains.annotations.NotNull;

public record FlightGadget(int mode, int priority) implements IClayGadget {

    private static final ResourceLocation MODIFIER_ID = Clayium.id("gadget_flight");

    @Override
    @NotNull
    public ResourceLocation getCategory(@NotNull ItemStack stack) {
        return ClayGadgetCategories.FLIGHT;
    }

    @Override
    public int getPriority(@NotNull ItemStack stack) {
        return priority;
    }

    @Override
    public void onActivated(@NotNull ClayGadgetContext context) {
        apply(context);
    }

    @Override
    public void tick(@NotNull ClayGadgetContext context) {
        Vec3 movement = context.player().getDeltaMovement();
        if (movement.y >= 0.0D) {
            context.player().fallDistance = 0.0F;
        } else {
            int ticks = (int) (-movement.y / 0.05D);
            context.player().fallDistance = Math.min(context.player().fallDistance,
                    ticks * (ticks - 1) * 0.025F);
        }
    }

    @Override
    public void onDeactivated(@NotNull ClayGadgetContext context) {
        AttributeInstance attribute = context.player().getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (attribute != null) {
            attribute.removeModifier(MODIFIER_ID);
        }
        if (!context.player().mayFly()) {
            context.player().getAbilities().flying = false;
        }
        context.player().onUpdateAbilities();
    }

    private static void apply(ClayGadgetContext context) {
        AttributeInstance attribute = context.player().getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (attribute != null) {
            attribute.addOrUpdateTransientModifier(
                    new AttributeModifier(MODIFIER_ID, 1.0D, AttributeModifier.Operation.ADD_VALUE));
        }
        context.player().onUpdateAbilities();
    }
}
