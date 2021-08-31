package de.intelligence.bachelorarbeit.simplifx.controller;

/**
 * An enum which defines the available visibility states of a controller.
 */
public enum VisibilityState {

    UNDEFINED,
    SHOWN,
    HIDDEN,
    GROUP_SHOWN,
    GROUP_HIDDEN;

    VisibilityState type() {
        if (this == UNDEFINED) {
            return UNDEFINED;
        }
        if (this == SHOWN || this == GROUP_SHOWN) {
            return SHOWN;
        }
        return HIDDEN;
    }

}
