package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ControllerVisibilityContext {

    private final ObjectProperty<VisibilityState> state;

    public ControllerVisibilityContext() {
        this.state = new SimpleObjectProperty<>(VisibilityState.UNDEFINED);
    }

    public VisibilityState getState() {
        return this.state.get();
    }

    public void setState(VisibilityState state) {
        this.state.set(state);
    }

    public ObjectProperty<VisibilityState> stateProperty() {
        return state;
    }

    enum VisibilityState {

        UNDEFINED,
        SHOWN,
        HIDDEN,
        GROUP_HIDDEN

    }


}
