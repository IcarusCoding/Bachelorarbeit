package de.intelligence.bachelorarbeit.simplifx.controller;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;

public interface IControllerGroupWrapper {

    void switchController(IController handler, IWrapperAnimationFactory animationFactory);

    void setController(IController handler);

    ReadOnlyObjectProperty<Pane> wrapperProperty();

    Pane getWrapper();

}
