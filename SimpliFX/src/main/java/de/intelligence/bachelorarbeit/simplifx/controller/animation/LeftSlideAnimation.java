package de.intelligence.bachelorarbeit.simplifx.controller.animation;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * An {@link IWrapperAnimation} which will use a left slide animation on a controller switch.
 */
public final class LeftSlideAnimation extends AbstractWrapperAnimation {

    /**
     * Creates a new {@link LeftSlideAnimation}.
     *
     * @param duration The {@link Duration} of the animation.
     */
    public LeftSlideAnimation(Duration duration) {
        super(duration);
    }

    @Override
    public Timeline create(Pane root, Pane before, Pane after) {
        root.getChildren().setAll(before, after);
        before.setTranslateX(-root.getWidth());
        final DoubleProperty speedFactor = new SimpleDoubleProperty();
        root.translateXProperty().bind(speedFactor.multiply(root.widthProperty()));
        return super.createDefaultTimeline(speedFactor, () -> {
            root.getChildren().setAll(after);
            before.setTranslateX(0);
            root.translateXProperty().unbind();
            root.setTranslateX(0);
        });
    }
}
