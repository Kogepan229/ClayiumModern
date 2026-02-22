package net.kogepan.clayium.capability.filter.data;

import net.kogepan.clayium.items.filter.FilterItemHelper;
import net.kogepan.clayium.registries.ClayiumFilterTypes;

import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Serializable payload for unlocalized-name filter data (regex string).
 */
public class UnlocalizedNameFilterData implements ItemFilterData {

    public static final MapCodec<UnlocalizedNameFilterData> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.optionalFieldOf("pattern", "").forGetter(UnlocalizedNameFilterData::pattern))
                    .apply(instance, UnlocalizedNameFilterData::new));

    private final String pattern;
    @Nullable
    private final Pattern compiledPattern;

    public UnlocalizedNameFilterData(@NotNull String pattern) {
        this.pattern = pattern;
        this.compiledPattern = compilePattern(pattern);
    }

    @NotNull
    public static UnlocalizedNameFilterData fromStack(@NotNull ItemStack stack) {
        return new UnlocalizedNameFilterData(FilterItemHelper.getFilterString(stack));
    }

    @NotNull
    public String pattern() {
        return pattern;
    }

    @Override
    @NotNull
    public ItemFilterType<?> type() {
        return ClayiumFilterTypes.UNLOCALIZED_NAME.get();
    }

    @Override
    public boolean test(@NotNull ItemStack stack) {
        if (compiledPattern == null) {
            return false;
        }
        return compiledPattern.matcher(stack.getDescriptionId()).find();
    }

    @Nullable
    private static Pattern compilePattern(@NotNull String patternText) {
        if (patternText.isEmpty()) {
            return null;
        }
        try {
            return Pattern.compile(patternText);
        } catch (PatternSyntaxException ignored) {
            return null;
        }
    }
}
