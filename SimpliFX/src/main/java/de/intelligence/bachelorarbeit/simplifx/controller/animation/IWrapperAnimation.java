package de.intelligence.bachelorarbeit.simplifx.controller.animation;

import javafx.animation.Timeline;
import javafx.scene.layout.Pane;

public interface IWrapperAnimation {

    Timeline create(Pane root, Pane before, Pane after);

}
