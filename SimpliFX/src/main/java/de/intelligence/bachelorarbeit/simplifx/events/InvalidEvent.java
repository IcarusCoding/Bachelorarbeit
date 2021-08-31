package de.intelligence.bachelorarbeit.simplifx.events;

/**
 * An event which will automatically be emitted from an {@link de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter}
 * if an event was fired which is not handled by any {@link de.intelligence.bachelorarbeit.simplifx.event.EventHandler}.
 */
public final class InvalidEvent {

    private final Object event;

    public InvalidEvent(Object event) {
        this.event = event;
    }

    public Object getEvent() {
        return this.event;
    }

}
