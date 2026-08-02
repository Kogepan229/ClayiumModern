package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.utils.MachineIOMode;
import net.kogepan.clayium.utils.MachineIOModes;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.model.data.ModelProperty;

import java.util.ArrayList;
import java.util.List;

public record ClayContainerModelData(
                                     List<MachineIOMode> inputModes,
                                     List<MachineIOMode> outputModes,
                                     int filterSides) {

    private static final Direction[] DIRECTIONS = Direction.values();

    public static final ModelProperty<ClayContainerModelData> PROPERTY = new ModelProperty<>();
    public static final ClayContainerModelData EMPTY = new ClayContainerModelData(
            List.of(
                    MachineIOMode.NONE,
                    MachineIOMode.NONE,
                    MachineIOMode.NONE,
                    MachineIOMode.NONE,
                    MachineIOMode.NONE,
                    MachineIOMode.NONE),
            List.of(
                    MachineIOMode.NONE,
                    MachineIOMode.NONE,
                    MachineIOMode.NONE,
                    MachineIOMode.NONE,
                    MachineIOMode.NONE,
                    MachineIOMode.NONE),
            0);

    public ClayContainerModelData {
        inputModes = List.copyOf(inputModes);
        outputModes = List.copyOf(outputModes);
        if (inputModes.size() != DIRECTIONS.length || outputModes.size() != DIRECTIONS.length) {
            throw new IllegalArgumentException("Clay container model data requires all six faces");
        }
    }

    public static ClayContainerModelData snapshot(
                                                  MachineIOModes inputModes,
                                                  MachineIOModes outputModes,
                                                  int filterSides) {
        List<MachineIOMode> inputSnapshot = new ArrayList<>(DIRECTIONS.length);
        List<MachineIOMode> outputSnapshot = new ArrayList<>(DIRECTIONS.length);
        for (Direction side : DIRECTIONS) {
            inputSnapshot.add(inputModes.getMode(side));
            outputSnapshot.add(outputModes.getMode(side));
        }
        return new ClayContainerModelData(inputSnapshot, outputSnapshot, filterSides);
    }

    public MachineIOMode inputMode(Direction side) {
        return this.inputModes.get(side.get3DDataValue());
    }

    public MachineIOMode outputMode(Direction side) {
        return this.outputModes.get(side.get3DDataValue());
    }

    public boolean hasFilter(Direction side) {
        return (this.filterSides & 1 << side.get3DDataValue()) != 0;
    }
}
