package de.intelligence.bachelorarbeit.simplifx.controller.animation;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * An {@link IWrapperAnimation} which fades into a new controller on a controller switch.
 */
public final class FadeAnimation extends AbstractWrapperAnimation {

    /**
     * Creates a new {@link FadeAnimation}.
     *
     * @param duration The {@link Duration} of the animation.
     */
    public FadeAnimation(Duration duration) {
        super(duration);
    }

    @Override
    public Timeline create(Pane root, Pane before, Pane after) {
        root.getChildren().setAll(after, before);
        final DoubleProperty factor = new SimpleDoubleProperty();
        before.opacityProperty().bind(factor);
        return super.createDefaultTimeline(factor, () -> {
            root.getChildren().setAll(after);
            before.opacityProperty().unbind();
            before.setOpacity(1);
        });
    }

}
