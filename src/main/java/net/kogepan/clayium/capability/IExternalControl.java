package net.kogepan.clayium.capability;

/**
 * External work control exposed by machines that can be operated through a Redstone Interface.
 *
 * <p>
 * Add-ons can expose an implementation through {@link ClayiumCapabilities#EXTERNAL_CONTROL}
 * during the NeoForge capability registration event.
 */
public interface IExternalControl {

    /** Queues one additional operation and enables work until the queued operation completes. */
    void doWorkOnce();

    /** Enables continuous work. */
    void startWork();

    /** Disables work. */
    void stopWork();

    /** Returns whether the machine currently has work that can be performed. */
    boolean isScheduled();

    /** Returns whether the machine performed work during its latest tick. */
    boolean isDoingWork();
}
