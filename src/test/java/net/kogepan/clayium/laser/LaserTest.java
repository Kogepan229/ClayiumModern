package net.kogepan.clayium.laser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LaserTest {

    private static final double EPSILON = 1.0E-12;

    @Test
    void rawEnergyIsZeroForNoColorLaser() {
        Laser laser = new Laser(0, 0, 0);
        assertEquals(0.0, laser.rawEnergy(), EPSILON);
    }

    @Test
    void calculatesExpectedRawEnergyForTierSampleLasers() {
        assertEquals(0.493174154155451, new Laser(1, 0, 0).rawEnergy(), EPSILON);
        assertEquals(0.785973181960941, new Laser(0, 1, 0).rawEnergy(), EPSILON);
        assertEquals(1.460226149452476, new Laser(0, 0, 1).rawEnergy(), EPSILON);
        assertEquals(598.395857989328300, new Laser(3, 3, 3).rawEnergy(), EPSILON);
    }

    @Test
    void calculatesExpectedRawEnergyForMixedLaser() {
        Laser laser = new Laser(2, 1, 4);
        assertEquals(231.641773712002250, laser.rawEnergy(), EPSILON);
    }

    @Test
    void rawEnergyCalculationDoesNotDependOnAge() {
        Laser oldLaser = new Laser(2, 1, 4, 0);
        Laser agedLaser = new Laser(2, 1, 4, 200);
        assertEquals(oldLaser.rawEnergy(), agedLaser.rawEnergy(), EPSILON);
    }

    @Test
    void defaultAgeIsZero() {
        Laser laser = new Laser(2, 1, 4);
        assertEquals(0, laser.age());
    }

    @Test
    void nbtRoundTripKeepsAllFields() throws ReflectiveOperationException {
        Assumptions.assumeTrue(isClassPresent("net.minecraft.nbt.CompoundTag"),
                "Skipping NBT round-trip test because Minecraft NBT classes are unavailable in this JUnit runtime.");
        Laser original = new Laser(2, 1, 4, 120);
        Object tag = invokeMethod(original, "toTag");
        Method fromTag = Laser.class.getMethod("fromTag", tag.getClass());
        Laser decoded = (Laser) fromTag.invoke(null, tag);
        assertEquals(original, decoded);
    }

    @Test
    void codecRoundTripKeepsAllFields() throws ReflectiveOperationException, ClassNotFoundException {
        Laser original = new Laser(2, 1, 4, 120);
        Object codec = Laser.class.getField("CODEC").get(null);
        Object jsonOps = Class.forName("com.mojang.serialization.JsonOps").getField("INSTANCE").get(null);

        Object encodedResult = invokeMethod(codec, "encodeStart", jsonOps, original);
        Object encoded = unwrapDataResult(encodedResult, "encode");

        Object decodedResult = invokeMethod(codec, "parse", jsonOps, encoded);
        Laser decoded = (Laser) unwrapDataResult(decodedResult, "decode");
        assertEquals(original, decoded);
    }

    @Test
    void streamCodecRoundTripKeepsAllFields() throws IOException {
        Laser original = new Laser(2, 1, 4, 120);
        byte[] payload;

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream output = new DataOutputStream(bytes)) {
            original.encode(output);
            payload = bytes.toByteArray();
        }

        Laser decoded;
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload))) {
            decoded = Laser.decode(input);
        }

        assertEquals(original, decoded);
    }

    private static Object unwrapDataResult(Object dataResult, String operation) throws ReflectiveOperationException {
        @SuppressWarnings("unchecked")
        Optional<Object> optional = (Optional<Object>) invokeMethod(dataResult, "result");
        return optional.orElseThrow(() -> new AssertionError("Failed to " + operation + " laser"));
    }

    private static Object invokeMethod(Object instance, String methodName, Object... args) throws ReflectiveOperationException {
        Method method = findMethod(instance.getClass(), methodName, args.length);
        return method.invoke(instance, args);
    }

    private static Method findMethod(Class<?> clazz, String methodName, int parameterCount) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == parameterCount) {
                return method;
            }
        }
        throw new AssertionError(
                "Method not found: " + clazz.getName() + "#" + methodName + " with " + parameterCount + " parameters");
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }
}
