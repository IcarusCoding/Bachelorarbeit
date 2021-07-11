package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.animation.Timeline;
import javafx.scene.layout.Pane;

public class DefaultWrapperAnimationFactory implements IWrapperAnimationFactory {

    @Override
    public Timeline createForPrevious(Pane pane) {
        return null;
    }

    @Override
    public Timeline createForNext(Pane pane) {
        return null;
    }

}
