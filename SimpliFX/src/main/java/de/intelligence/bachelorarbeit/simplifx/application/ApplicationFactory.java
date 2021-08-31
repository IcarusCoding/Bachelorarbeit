package de.intelligence.bachelorarbeit.simplifx.application;

import javafx.application.Application;
import javafx.application.Preloader;

/**
 * A factory for creating an {@link Application} by an optional {@link Preloader}.
 */
public interface ApplicationFactory {

    /**
     * Creates a new {@link Application} instance.
     *
     * @param preloader The optional {@link Preloader} instance for the application.
     * @return The new {@link Application} instance.
     */
    Application create(Preloader preloader);

}
