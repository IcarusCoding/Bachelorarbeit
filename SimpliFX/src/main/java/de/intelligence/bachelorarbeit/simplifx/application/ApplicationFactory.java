package de.intelligence.bachelorarbeit.simplifx.application;

import javafx.application.Application;
import javafx.application.Preloader;

/**
 * A factory for creating an {@link Application} by an optional {@link Preloader}.
 */
public interface ApplicationFactory {

    Application create(Preloader preloader);

}
