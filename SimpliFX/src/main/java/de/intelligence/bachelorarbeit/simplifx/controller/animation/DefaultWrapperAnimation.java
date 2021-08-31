package de.intelligence.bachelorarbeit.simplifx.controller.animation;

import javafx.animation.Timeline;
import javafx.scene.layout.Pane;

/**
 * An {@link IWrapperAnimation} which switches a controller instantly without any animation effects.
 */
public final class DefaultWrapperAnimation implements IWrapperAnimation {

    @Override
    public Timeline create(Pane root, Pane before, Pane after) {
        return null;
    }

}
