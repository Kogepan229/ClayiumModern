package net.kogepan.clayium.items.gadget.behavior;

import net.kogepan.clayium.api.gadget.ClayGadgetCategories;
import net.kogepan.clayium.api.gadget.ClayGadgetContext;
import net.kogepan.clayium.api.gadget.IClayGadget;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.items.filter.FilterItemHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Automatic food consumption with the Original economical timing algorithm. */
public record AutoEatGadget(boolean economical) implements IClayGadget {

    @Override
    @NotNull
    public ResourceLocation getCategory(@NotNull ItemStack stack) {
        return ClayGadgetCategories.AUTO_EAT;
    }

    @Override
    public void tick(@NotNull ClayGadgetContext context) {
        if (!(context.player() instanceof ServerPlayer player) || !player.getFoodData().needsFood()) {
            return;
        }

        int foodLevel = player.getFoodData().getFoodLevel();
        float saturationLevel = player.getFoodData().getSaturationLevel();
        float bestRate = 0.0F;
        int bestIndex = -1;
        boolean bestTiming = true;
        Inventory inventory = player.getInventory();

        for (int index = 0; index < inventory.items.size(); index++) {
            ItemStack candidate = inventory.items.get(index);
            FoodProperties food = candidate.getFoodProperties(player);
            if (food == null || !matchesFilter(context.stack(), candidate)) {
                continue;
            }

            int nutrition = food.nutrition();
            float saturation = food.saturation();
            if (nutrition <= 0 && saturation <= 0.0F) {
                continue;
            }
            float currentValue = usefulValue(foodLevel, saturationLevel, nutrition, saturation);
            boolean optimalNow = true;
            if (economical) {
                int simulatedFood = foodLevel;
                float simulatedSaturation = saturationLevel;
                while (simulatedSaturation > 0.0F || simulatedFood > 0) {
                    if (simulatedSaturation > 0.0F) {
                        simulatedSaturation--;
                    }
                    if (simulatedSaturation <= 0.0F) {
                        simulatedSaturation = 0.0F;
                        simulatedFood--;
                    }
                    float laterValue = usefulValue(simulatedFood, simulatedSaturation, nutrition, saturation);
                    if (laterValue > currentValue) {
                        currentValue = laterValue;
                        optimalNow = false;
                    }
                }
            }

            float rate = currentValue / (nutrition + saturation);
            if (bestRate < rate) {
                bestRate = rate;
                bestIndex = index;
                bestTiming = optimalNow;
            }
        }

        if (bestIndex >= 0 && bestTiming) {
            ItemStack food = inventory.items.get(bestIndex);
            inventory.items.set(bestIndex, food.finishUsingItem(player.level(), player));
            inventory.setChanged();
        }
    }

    private static float usefulValue(int foodLevel, float saturationLevel, int nutrition, float saturation) {
        return Math.min(nutrition, 20 - foodLevel) +
                Math.min(saturation, Math.min(foodLevel + nutrition, 20) - saturationLevel);
    }

    private static boolean matchesFilter(ItemStack gadget, ItemStack candidate) {
        List<ItemStack> filters = FilterItemHelper.readItems(gadget);
        boolean hasFilter = false;
        for (ItemStack filter : filters) {
            if (filter.isEmpty()) {
                continue;
            }
            hasFilter = true;
            ItemFilterData nested = filter.getCapability(ClayiumCapabilities.ITEM_FILTER_DATA);
            if (nested != null ? nested.test(candidate) : ItemStack.isSameItemSameComponents(filter, candidate)) {
                return true;
            }
        }
        return !hasFilter;
    }
}
