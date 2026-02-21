package net.kogepan.clayium.capability.filter;

import net.kogepan.clayium.capability.IItemFilter;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Filter that matches stacks whose description id (translation key) matches a regex pattern.
 */
public class UnlocalizedNameItemFilter implements IItemFilter {

    private final Pattern pattern;

    public UnlocalizedNameItemFilter(String filterString) {
        Pattern p = null;
        if (filterString != null && !filterString.isEmpty()) {
            try {
                p = Pattern.compile(filterString);
            } catch (PatternSyntaxException ignored) {
                // invalid regex -> no match
            }
        }
        this.pattern = p;
    }

    @Override
    public boolean test(@NotNull ItemStack stack) {
        if (pattern == null) {
            return false;
        }
        return pattern.matcher(stack.getDescriptionId()).find();
    }
}
