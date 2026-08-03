package net.kogepan.clayium.api.configuration;

import net.minecraft.core.Direction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/** Immutable machine IO settings stored by a Memory Card. */
public record MachineIOConfiguration(List<MachineIOMode> inputModes, List<MachineIOMode> outputModes,
                                     Direction facing, boolean supportsVerticalFacing, boolean pipe) {

    private static final int SIDE_COUNT = Direction.values().length;
    private static final Codec<List<MachineIOMode>> MODES_CODEC = MachineIOMode.CODEC.listOf()
            .validate(modes -> modes.size() == SIDE_COUNT ?
                    DataResult.success(modes) :
                    DataResult.error(() -> "Expected " + SIDE_COUNT + " machine IO modes, got " + modes.size()));

    public static final Codec<MachineIOConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MODES_CODEC.fieldOf("input_modes").forGetter(MachineIOConfiguration::inputModes),
            MODES_CODEC.fieldOf("output_modes").forGetter(MachineIOConfiguration::outputModes),
            Direction.CODEC.fieldOf("facing").forGetter(MachineIOConfiguration::facing),
            Codec.BOOL.fieldOf("supports_vertical_facing")
                    .forGetter(MachineIOConfiguration::supportsVerticalFacing),
            Codec.BOOL.fieldOf("pipe").forGetter(MachineIOConfiguration::pipe))
            .apply(instance, MachineIOConfiguration::new));

    public MachineIOConfiguration {
        if (inputModes.size() != SIDE_COUNT || outputModes.size() != SIDE_COUNT) {
            throw new IllegalArgumentException("Machine IO settings must contain one mode for every direction");
        }
        inputModes = List.copyOf(inputModes);
        outputModes = List.copyOf(outputModes);
    }

    public MachineIOModes createInputModes() {
        return this.createModes(this.inputModes);
    }

    public MachineIOModes createOutputModes() {
        return this.createModes(this.outputModes);
    }

    private MachineIOModes createModes(List<MachineIOMode> modes) {
        MachineIOModes result = new MachineIOModes();
        for (Direction direction : Direction.values()) {
            result.setMode(direction, modes.get(direction.get3DDataValue()));
        }
        return result;
    }
}
