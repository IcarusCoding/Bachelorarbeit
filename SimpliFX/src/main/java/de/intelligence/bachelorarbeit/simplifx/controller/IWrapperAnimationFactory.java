package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.animation.Timeline;
import javafx.scene.layout.Pane;

public interface IWrapperAnimationFactory {

    Timeline createForPrevious(Pane pane);

    Timeline createForNext(Pane pane);

}
