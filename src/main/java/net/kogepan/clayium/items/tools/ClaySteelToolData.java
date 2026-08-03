package net.kogepan.clayium.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import java.util.LinkedHashSet;
import java.util.List;

public record ClaySteelToolData(int mode, List<BlockPos> customOffsets) {

    public static final int MAX_RANGE = 4;
    private static final int MAX_DIAMETER = MAX_RANGE * 2 + 1;
    public static final int MAX_CUSTOM_OFFSETS = MAX_DIAMETER * MAX_DIAMETER * MAX_DIAMETER;
    public static final ClaySteelToolData EMPTY = new ClaySteelToolData(0, List.of());

    private static final Codec<List<BlockPos>> CUSTOM_OFFSETS_CODEC = BlockPos.CODEC
            .sizeLimitedListOf(MAX_CUSTOM_OFFSETS)
            .validate(ClaySteelToolData::validateOffsets);

    public static final Codec<ClaySteelToolData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, MAX_RANGE)
                    .optionalFieldOf("mode", 0)
                    .forGetter(ClaySteelToolData::mode),
            CUSTOM_OFFSETS_CODEC
                    .optionalFieldOf("custom_offsets", List.of())
                    .forGetter(ClaySteelToolData::customOffsets))
            .apply(instance, ClaySteelToolData::new));

    public static final StreamCodec<ByteBuf, ClaySteelToolData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClaySteelToolData::mode,
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list(MAX_CUSTOM_OFFSETS)),
            ClaySteelToolData::customOffsets,
            ClaySteelToolData::new);

    public ClaySteelToolData {
        if (mode < 0 || mode > MAX_RANGE) {
            throw new IllegalArgumentException("Mode is outside the supported range: " + mode);
        }
        LinkedHashSet<BlockPos> uniqueOffsets = new LinkedHashSet<>(customOffsets);
        if (uniqueOffsets.size() > MAX_CUSTOM_OFFSETS) {
            throw new IllegalArgumentException("Too many custom mining offsets: " + customOffsets.size());
        }
        if (!uniqueOffsets.isEmpty() && !uniqueOffsets.contains(BlockPos.ZERO)) {
            throw new IllegalArgumentException("Custom mining offsets do not include the origin");
        }
        for (BlockPos offset : uniqueOffsets) {
            if (isOutsideSupportedRange(offset)) {
                throw new IllegalArgumentException("Custom mining offset is outside the supported range: " + offset);
            }
        }
        customOffsets = List.copyOf(uniqueOffsets);
    }

    public ClaySteelToolData withMode(int newMode) {
        return newMode == this.mode ? this : new ClaySteelToolData(newMode, this.customOffsets);
    }

    public ClaySteelToolData withCustomOffsets(List<BlockPos> newCustomOffsets) {
        return new ClaySteelToolData(this.mode, newCustomOffsets);
    }

    private static DataResult<List<BlockPos>> validateOffsets(List<BlockPos> offsets) {
        if (!offsets.isEmpty() && !offsets.contains(BlockPos.ZERO)) {
            return DataResult.error(() -> "Custom mining offsets do not include the origin");
        }
        for (BlockPos offset : offsets) {
            if (isOutsideSupportedRange(offset)) {
                return DataResult.error(() -> "Custom mining offset is outside the supported range: " + offset);
            }
        }
        return DataResult.success(offsets);
    }

    private static boolean isOutsideSupportedRange(BlockPos offset) {
        return offset.getX() < -MAX_RANGE || offset.getX() > MAX_RANGE || offset.getY() < -MAX_RANGE ||
                offset.getY() > MAX_RANGE || offset.getZ() < -MAX_RANGE || offset.getZ() > MAX_RANGE;
    }
}
