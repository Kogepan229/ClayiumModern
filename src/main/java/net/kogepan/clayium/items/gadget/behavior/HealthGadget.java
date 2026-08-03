package net.kogepan.clayium.items.gadget.behavior;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.api.gadget.ClayGadgetCategories;
import net.kogepan.clayium.api.gadget.ClayGadgetContext;
import net.kogepan.clayium.api.gadget.IClayGadget;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public record HealthGadget(double amount, int priority) implements IClayGadget {

    private static final ResourceLocation MODIFIER_ID = Clayium.id("gadget_health");

    @Override
    @NotNull
    public ResourceLocation getCategory(@NotNull ItemStack stack) {
        return ClayGadgetCategories.HEALTH;
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
    public void onDeactivated(@NotNull ClayGadgetContext context) {
        AttributeInstance attribute = context.player().getAttribute(Attributes.MAX_HEALTH);
        if (attribute != null) {
            attribute.removeModifier(MODIFIER_ID);
            context.player().setHealth(Math.min(context.player().getHealth(), context.player().getMaxHealth()));
        }
    }

    private void apply(ClayGadgetContext context) {
        AttributeInstance attribute = context.player().getAttribute(Attributes.MAX_HEALTH);
        if (attribute != null) {
            attribute.addOrUpdateTransientModifier(
                    new AttributeModifier(MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE));
        }
    }
}
