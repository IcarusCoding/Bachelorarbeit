package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Keeps track of a {@link VisibilityState} and provides information about the hide and show count.
 */
public final class VisibilityContext {

    private final ReadOnlyObjectWrapper<VisibilityState> visibility;

    private int showCount;
    private int hideCount;

    VisibilityContext(ObjectProperty<VisibilityState> visibility) {
        this.visibility = new ReadOnlyObjectWrapper<>(VisibilityState.UNDEFINED);
        this.visibility.bind(visibility);
    }

    void incrementShow() {
        this.showCount++;
    }

    void incrementHide() {
        this.hideCount++;
    }

    /**
     * Checks if there was only one state switch to {@link VisibilityState#SHOWN}.
     *
     * @return If there was only one state switch to {@link VisibilityState#SHOWN}.
     */
    public boolean isFirstShow() {
        return this.showCount == 1;
    }

    /**
     * Checks if there was only one state switch to {@link VisibilityState#HIDDEN}.
     *
     * @return If there was only one state switch to {@link VisibilityState#HIDDEN}.
     */
    public boolean isFirstHide() {
        return this.hideCount == 1;
    }

    /**
     * Gets the number of times that a state switch to {@link VisibilityState#HIDDEN} occurred.
     *
     * @return The number of times that a state switch to {@link VisibilityState#HIDDEN} occurred.
     */
    public int getHideCount() {
        return this.hideCount;
    }

    /**
     * Gets the number of times that a state switch to {@link VisibilityState#SHOWN} occurred.
     *
     * @return The number of times that a state switch to {@link VisibilityState#SHOWN} occurred.
     */
    public int getShowCount() {
        return this.showCount;
    }

    /**
     * Retrieves the current {@link VisibilityState}.
     *
     * @return The current {@link VisibilityState}.
     */
    public VisibilityState getVisibility() {
        return this.visibility.get();
    }

    /**
     * Retrieves the current {@link VisibilityState} as a {@link ReadOnlyObjectProperty}.
     *
     * @return The current {@link VisibilityState} as a {@link ReadOnlyObjectProperty}.
     */
    public ReadOnlyObjectProperty<VisibilityState> visibilityProperty() {
        return this.visibility.getReadOnlyProperty();
    }

}
