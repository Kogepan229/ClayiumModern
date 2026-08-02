package net.kogepan.clayium;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue PROGRESSION_RATE = BUILDER
            .gameRestart()
            .comment("Progression speed multiplier. Values above 1 speed up progression; values below 1 slow it down.")
            .defineInRange("progressionRate", 1.0D, 0.001D, 9999.0D);

    public static final ModConfigSpec.IntValue LASER_QUALITY = BUILDER
            .comment("Visual quality of clay laser beam. 2 is recommended for low-end computers.")
            .defineInRange("laserQuality", 8, 1, 32);

    public static final ModConfigSpec.IntValue MAX_CLAY_LASER_LENGTH = BUILDER
            .comment("Maximum length of clay laser beam in blocks.")
            .defineInRange("maxClayLaserLength", 32, 1, 256);

    public static final ModConfigSpec.BooleanValue INTERFACE_TARGET_HIGHLIGHT_OUTLINE = BUILDER
            .comment("Whether Clay Interface target highlight renders a box outline.")
            .define("interfaceTargetHighlightOutline", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
