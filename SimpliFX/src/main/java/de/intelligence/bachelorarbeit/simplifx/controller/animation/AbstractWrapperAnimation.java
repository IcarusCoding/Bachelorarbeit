package de.intelligence.bachelorarbeit.simplifx.controller.animation;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.util.Duration;

/**
 * An abstract implementation for the {@link IWrapperAnimation} interface.
 */
public abstract class AbstractWrapperAnimation implements IWrapperAnimation {

    protected Duration duration;

    public AbstractWrapperAnimation(Duration duration) {
        this.duration = duration;
    }

    /**
     * Creates a default {@link Timeline} configuration.
     *
     * @param factor A {@link DoubleProperty} which will be animated from 1 to 0 in {@link this#duration}.
     * @param onStop A {@link Runnable} which will be called when the animation finishes or gets stopped forcefully.
     * @return A pre-configured {@link Timeline} instance.
     */
    protected Timeline createDefaultTimeline(DoubleProperty factor, Runnable onStop) {
        final Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(factor, 1)),
                new KeyFrame(duration, new KeyValue(factor, 0, Interpolator.EASE_BOTH)));
        timeline.statusProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals(Animation.Status.STOPPED)) {
                onStop.run();
            }
        });
        return timeline;
    }

}
