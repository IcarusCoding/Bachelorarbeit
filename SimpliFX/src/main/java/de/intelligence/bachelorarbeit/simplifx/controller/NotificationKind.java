package de.intelligence.bachelorarbeit.simplifx.controller;

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
