package net.kogepan.clayium.laser;

import net.minecraft.nbt.CompoundTag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Immutable laser data model.
 * <p>
 * Energy calculation follows ClayiumUnofficial / ClayiumOriginal behavior.
 *
 * @param red   Red channel intensity
 * @param green Green channel intensity
 * @param blue  Blue channel intensity
 * @param age   Laser age
 */
public record Laser(int red, int green, int blue, int age) {

    public static final Codec<Laser> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("red").forGetter(Laser::red),
            Codec.INT.fieldOf("green").forGetter(Laser::green),
            Codec.INT.fieldOf("blue").forGetter(Laser::blue),
            Codec.INT.optionalFieldOf("age", 0).forGetter(Laser::age))
            .apply(instance, Laser::new));

    private static final double DAMPING_RATE = 0.1;
    private static final double[] BASES = { 2.5, 1.8, 1.5 };
    private static final double[] MAX_ENERGIES = { 1000.0, 300.0, 100.0 };
    private static final String TAG_RED = "red";
    private static final String TAG_GREEN = "green";
    private static final String TAG_BLUE = "blue";
    private static final String TAG_AGE = "age";

    public Laser(int red, int green, int blue) {
        this(red, green, blue, 0);
    }

    /**
     * Calculates laser energy from current RGB values.
     *
     * @return Energy value
     */
    public double energy() {
        return calculateEnergyPerColor(blue, BASES[0], MAX_ENERGIES[0], DAMPING_RATE)
                * calculateEnergyPerColor(green, BASES[1], MAX_ENERGIES[1], DAMPING_RATE)
                * calculateEnergyPerColor(red, BASES[2], MAX_ENERGIES[2], DAMPING_RATE)
                - 1.0;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(TAG_RED, red);
        tag.putInt(TAG_GREEN, green);
        tag.putInt(TAG_BLUE, blue);
        tag.putInt(TAG_AGE, age);
        return tag;
    }

    public static Laser fromTag(CompoundTag tag) {
        return new Laser(tag.getInt(TAG_RED), tag.getInt(TAG_GREEN), tag.getInt(TAG_BLUE), tag.getInt(TAG_AGE));
    }

    public void encode(DataOutput output) throws IOException {
        output.writeInt(red);
        output.writeInt(green);
        output.writeInt(blue);
        output.writeInt(age);
    }

    public static Laser decode(DataInput input) throws IOException {
        return new Laser(input.readInt(), input.readInt(), input.readInt(), input.readInt());
    }

    private static double calculateEnergyPerColor(int colorAmount, double base, double maxEnergy, double dampingRate) {
        if (colorAmount <= 0 || dampingRate <= 0.0 || maxEnergy < 0.0 || base < 1.0) {
            return 1.0;
        }

        double amount = colorAmount;
        double r1 = dampingRate + 1.0;
        double c = Math.pow(base, r1 * (Math.log(r1 / dampingRate) / Math.log(maxEnergy)));
        double aiTop = Math.log(r1 / (Math.pow(c, -amount) + dampingRate));
        double aiBottom = Math.log(r1 / dampingRate);
        double ai = aiTop / aiBottom;
        double energy = Math.pow(maxEnergy, ai)
                * ((1 + dampingRate * colorAmount * Math.pow(c, amount)) / (1 + dampingRate * Math.pow(c, amount)));
        return Math.max(1.0, energy);
    }
}
