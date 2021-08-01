package de.intelligence.bachelorarbeit.simplifx.application;

import javafx.application.Application;
import javafx.application.Preloader;

public interface ApplicationFactory {

    Application create(Preloader preloader);

}
