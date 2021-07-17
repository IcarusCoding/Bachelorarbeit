package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class VisibilityContext {

    private final ReadOnlyObjectWrapper<VisibilityState> visibility;

    private int showCount;
    private int hideCount;

    public VisibilityContext(ObjectProperty<VisibilityState> visibility) {
        this.visibility = new ReadOnlyObjectWrapper<>(VisibilityState.UNDEFINED);
        this.visibility.bind(Bindings.createObjectBinding(() -> {
            final VisibilityState state = visibility.get();
            if (state.equals(VisibilityState.SHOWN)) {
                this.showCount++;
            } else if (state.equals(VisibilityState.HIDDEN)) {
                this.hideCount++;
            }
            return state;
        }, visibility));
    }

    public boolean isFirstShow() {
        return this.showCount == 1;
    }

    public boolean isFirstHide() {
        return this.hideCount == 1;
    }

    public VisibilityState getVisibility() {
        return this.visibility.get();
    }

    public ReadOnlyObjectProperty<VisibilityState> visibilityProperty() {
        return this.visibility.getReadOnlyProperty();
    }

}
