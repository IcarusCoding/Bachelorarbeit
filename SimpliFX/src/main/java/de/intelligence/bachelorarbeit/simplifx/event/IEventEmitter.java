package de.intelligence.bachelorarbeit.simplifx.event;

/**
 * An interface for emitting events and registering/unregistering event handlers.
 */
public interface IEventEmitter {

    /**
     * Emits the specified event object.
     *
     * @param obj The event object.
     */
    void emit(Object obj);

    /**
     * Registers a new object instance as an event handler.
     *
     * @param obj The event handler.
     */
    void register(Object obj);

    /**
     * Unregisters a new object instance.
     *
     * @param obj The event handler.
     */
    void unregister(Object obj);

}
