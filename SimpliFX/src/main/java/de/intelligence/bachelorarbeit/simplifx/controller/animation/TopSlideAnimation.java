package de.intelligence.bachelorarbeit.simplifx.controller.animation;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public final class TopSlideAnimation extends AbstractWrapperAnimation {

    public TopSlideAnimation(Duration duration) {
        super(duration);
    }

    @Override
    public Timeline create(Pane root, Pane before, Pane after) {
        root.getChildren().setAll(before, after);
        before.setTranslateY(-root.getHeight());
        final DoubleProperty speedFactor = new SimpleDoubleProperty();
        root.translateYProperty().bind(speedFactor.multiply(root.heightProperty()));
        return super.createDefaultTimeline(speedFactor, () -> {
            root.getChildren().setAll(after);
            before.setTranslateY(0);
            root.translateYProperty().unbind();
            root.setTranslateY(0);
        });
    }
}
