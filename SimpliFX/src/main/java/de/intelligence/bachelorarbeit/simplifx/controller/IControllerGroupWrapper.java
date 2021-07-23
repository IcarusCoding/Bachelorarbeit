package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;

import de.intelligence.bachelorarbeit.simplifx.controller.animation.IWrapperAnimation;

public interface IControllerGroupWrapper {

    void switchController(IController handler, IWrapperAnimation animationFactory);

    void setController(IController handler);

    ReadOnlyObjectProperty<Pane> wrapperProperty();

    Pane getWrapper();

}
