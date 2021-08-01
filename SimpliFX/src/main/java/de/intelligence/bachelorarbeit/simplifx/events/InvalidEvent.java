package de.intelligence.bachelorarbeit.simplifx.events;

public final class InvalidEvent {

    private final Object event;

    public InvalidEvent(Object event) {
        this.event = event;
    }

    public Object getEvent() {
        return this.event;
    }

}
