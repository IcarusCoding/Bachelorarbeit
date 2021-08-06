package de.intelligence.bachelorarbeit.simplifx.controller;

/**
 * An enum which defines the controller notification types.
 */
public enum NotificationKind {

    ERROR("#db5a5a"),
    WARN("#db875a"),
    INFO("#5a85db"),
    SUCCESS("#67db5a");

    private final String color;

    NotificationKind(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

}
