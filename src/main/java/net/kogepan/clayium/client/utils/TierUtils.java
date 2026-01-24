package net.kogepan.clayium.client.utils;

public class TierUtils {

    public static final int BUFFER_TIER_MIN = 4;
    public static final int BUFFER_TIER_MAX = 13;
    private static final int[] TRANSPORT_BUFFER_AUTO_IMPORT_MAX_DEFAULT = { 1, 4, 16, 64, 128,
            192, 256, 512, 1024, 6400 };
    private static final int[] TRANSPORT_BUFFER_AUTO_EXPORT_MAX_DEFAULT = { 1, 4, 16, 64, 128,
            192, 256, 512, 1024, 6400 };
    private static final int[] TRANSPORT_BUFFER_AUTO_IMPORT_INTERVAL_DEFAULT = { 8, 4, 2, 1, 1, 1, 1, 1,
            1, 1 };
    private static final int[] TRANSPORT_BUFFER_AUTO_EXPORT_INTERVAL_DEFAULT = { 8, 4, 2, 1, 1, 1, 1, 1,
            1, 1 };

    public static final int MACHINE_TIER_MIN = 1;
    public static final int MACHINE_TIER_MAX = 13;
    private static final int[] TRANSPORT_MACHINE_AUTO_IMPORT_MAX_DEFAULT = { 8, 8, 8, 8, 16, 64, 64, 64, 64, 64, 64, 64,
            64 };
    private static final int[] TRANSPORT_MACHINE_AUTO_EXPORT_MAX_DEFAULT = { 8, 8, 8, 8, 16, 64, 64, 64, 64, 64, 64, 64,
            64 };
    private static final int[] TRANSPORT_MACHINE_AUTO_IMPORT_INTERVAL_DEFAULT = { 20, 20, 20, 20, 2, 1, 1, 1, 1, 1, 1,
            1, 1 };
    private static final int[] TRANSPORT_MACHINE_AUTO_EXPORT_INTERVAL_DEFAULT = { 20, 20, 20, 20, 2, 1, 1, 1, 1, 1, 1,
            1, 1 };

    public static int getBufferAutoImportMax(int tier) {
        if (tier < BUFFER_TIER_MIN || tier > BUFFER_TIER_MAX) {
            throw new IllegalArgumentException("Invalid buffer tier: " + tier);
        }
        return TRANSPORT_BUFFER_AUTO_IMPORT_MAX_DEFAULT[tier - BUFFER_TIER_MIN];
    }

    public static int getBufferAutoExportMax(int tier) {
        if (tier < BUFFER_TIER_MIN || tier > BUFFER_TIER_MAX) {
            throw new IllegalArgumentException("Invalid buffer tier: " + tier);
        }
        return TRANSPORT_BUFFER_AUTO_EXPORT_MAX_DEFAULT[tier - BUFFER_TIER_MIN];
    }

    public static int getBufferAutoImportInterval(int tier) {
        if (tier < BUFFER_TIER_MIN || tier > BUFFER_TIER_MAX) {
            throw new IllegalArgumentException("Invalid buffer tier: " + tier);
        }
        return TRANSPORT_BUFFER_AUTO_IMPORT_INTERVAL_DEFAULT[tier - BUFFER_TIER_MIN];
    }

    public static int getBufferAutoExportInterval(int tier) {
        if (tier < BUFFER_TIER_MIN || tier > BUFFER_TIER_MAX) {
            throw new IllegalArgumentException("Invalid buffer tier: " + tier);
        }
        return TRANSPORT_BUFFER_AUTO_EXPORT_INTERVAL_DEFAULT[tier - BUFFER_TIER_MIN];
    }

    public static int getMachineAutoImportMax(int tier) {
        if (tier < MACHINE_TIER_MIN || tier > MACHINE_TIER_MAX) {
            throw new IllegalArgumentException("Invalid machine tier: " + tier);
        }
        return TRANSPORT_MACHINE_AUTO_IMPORT_MAX_DEFAULT[tier - MACHINE_TIER_MIN];
    }

    public static int getMachineAutoExportMax(int tier) {
        if (tier < MACHINE_TIER_MIN || tier > MACHINE_TIER_MAX) {
            throw new IllegalArgumentException("Invalid machine tier: " + tier);
        }
        return TRANSPORT_MACHINE_AUTO_EXPORT_MAX_DEFAULT[tier - MACHINE_TIER_MIN];
    }

    public static int getMachineAutoImportInterval(int tier) {
        if (tier < MACHINE_TIER_MIN || tier > MACHINE_TIER_MAX) {
            throw new IllegalArgumentException("Invalid machine tier: " + tier);
        }
        return TRANSPORT_MACHINE_AUTO_IMPORT_INTERVAL_DEFAULT[tier - MACHINE_TIER_MIN];
    }

    public static int getMachineAutoExportInterval(int tier) {
        if (tier < MACHINE_TIER_MIN || tier > MACHINE_TIER_MAX) {
            throw new IllegalArgumentException("Invalid machine tier: " + tier);
        }
        return TRANSPORT_MACHINE_AUTO_EXPORT_INTERVAL_DEFAULT[tier - MACHINE_TIER_MIN];
    }
}
