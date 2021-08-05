package de.intelligence.bachelorarbeit.simplifx.controller.animation;

import javafx.animation.Timeline;
import javafx.scene.layout.Pane;

/**
 * An interface for the creation of controller switch animations.
 */
public interface IWrapperAnimation {

    /**
     * Creates a new {@link Timeline} instance with the specified state of a controller group.
     *
     * @param root   The root {@link Pane} of the controller group.
     * @param before The {@link Pane} which was shown before.
     * @param after  The {@link Pane} which will be shown next.
     * @return A new {@link Timeline} instance.
     */
    Timeline create(Pane root, Pane before, Pane after);

}
