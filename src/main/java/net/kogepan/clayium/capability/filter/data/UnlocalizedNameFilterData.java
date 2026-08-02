package net.kogepan.clayium.capability.filter.data;

import net.kogepan.clayium.registries.ClayiumFilterTypes;

import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Serializable payload for a description-ID regular-expression filter. */
public final class UnlocalizedNameFilterData implements ItemFilterData {

    public static final MapCodec<UnlocalizedNameFilterData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.STRING.optionalFieldOf("pattern", "").forGetter(UnlocalizedNameFilterData::pattern))
            .apply(instance, UnlocalizedNameFilterData::new));

    private final String pattern;
    private final @Nullable Pattern compiledPattern;

    public UnlocalizedNameFilterData(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = compilePattern(pattern);
    }

    public String pattern() {
        return this.pattern;
    }

    @Override
    public ItemFilterType<?> type() {
        return ClayiumFilterTypes.UNLOCALIZED_NAME.get();
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.compiledPattern != null && this.compiledPattern.matcher(stack.getItem().getDescriptionId()).find();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof UnlocalizedNameFilterData that && this.pattern.equals(that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern);
    }

    private static @Nullable Pattern compilePattern(String pattern) {
        if (pattern.isEmpty()) {
            return null;
        }
        try {
            return Pattern.compile(pattern);
        } catch (PatternSyntaxException ignored) {
            return null;
        }
    }
}
