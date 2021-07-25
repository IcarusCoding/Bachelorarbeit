package de.intelligence.bachelorarbeit.simplifx.controller.animation;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public final class RightSlideAnimation extends AbstractWrapperAnimation {

    public RightSlideAnimation(Duration duration) {
        super(duration);
    }

    @Override
    public Timeline create(Pane root, Pane before, Pane after) {
        root.getChildren().setAll(before, after);
        before.setTranslateX(root.getWidth());
        final DoubleProperty speedFactor = new SimpleDoubleProperty();
        root.translateXProperty().bind(speedFactor.multiply(root.widthProperty().multiply(-1)));
        return super.createDefaultTimeline(speedFactor, () -> {
            root.getChildren().setAll(after);
            before.setTranslateX(0);
            root.translateXProperty().unbind();
            root.setTranslateX(0);
        });
    }
}
