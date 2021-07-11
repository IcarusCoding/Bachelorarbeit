package de.intelligence.bachelorarbeit.simplifx.events;

import javafx.application.Application;
import javafx.application.Preloader;

public class StateChangeEvent extends AbstractApplicationEvent {

    private final Preloader.StateChangeNotification.Type type;
    private final Application application;

    public StateChangeEvent(Preloader.StateChangeNotification.Type type, Application application,
                            Application.Parameters parameters) {
        super(parameters);
        this.type = type;
        this.application = application;
    }

    public Preloader.StateChangeNotification.Type getType() {
        return this.type;
    }

    public Application getApplication() {
        return this.application;
    }

}
