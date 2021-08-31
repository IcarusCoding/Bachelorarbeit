package de.intelligence.bachelorarbeit.simplifx.events;

import javafx.application.Application;

/**
 * An {@link AbstractApplicationEvent} for handling the {@link javafx.application.Preloader#handleErrorNotification} method invocation.
 */
public final class ErrorEvent extends AbstractApplicationEvent {

    private final Throwable cause;
    private final String details;
    private final String location;

    private boolean handled;

    public ErrorEvent(Throwable cause, String details, String location, Application.Parameters parameters) {
        super(parameters);
        this.cause = cause;
        this.details = details;
        this.location = location;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public String getDetails() {
        return this.details;
    }

    public String getLocation() {
        return this.location;
    }

    public void setHandled() {
        this.handled = true;
    }

    public boolean wasHandled() {
        return this.handled;
    }

}
