package de.intelligence.bachelorarbeit.simplifx.event.events;

public final class InvalidEvent extends AbstractEvent {

    private final Object event;

    public InvalidEvent(Object event) {
        this.event = event;
    }

    public Object getEvent() {
        return this.event;
    }

}
