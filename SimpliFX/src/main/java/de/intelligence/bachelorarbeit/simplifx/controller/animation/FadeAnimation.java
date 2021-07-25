package de.intelligence.bachelorarbeit.simplifx.controller.animation;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public final class FadeAnimation extends AbstractWrapperAnimation {

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
