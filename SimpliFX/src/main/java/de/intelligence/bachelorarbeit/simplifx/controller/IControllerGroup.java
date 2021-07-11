package de.intelligence.bachelorarbeit.simplifx.controller;

import java.io.IOException;

import javafx.scene.layout.Pane;

public interface IControllerGroup {

    Pane start(IControllerGroupWrapper wrapper) throws IOException;

    void destroy(Class<?> clazz);

    void destroy();

    Class<?> getStartController();

}
