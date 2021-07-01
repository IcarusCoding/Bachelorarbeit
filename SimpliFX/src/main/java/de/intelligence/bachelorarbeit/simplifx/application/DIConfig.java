package de.intelligence.bachelorarbeit.simplifx.application;

import javafx.application.Application;

import de.intelligence.bachelorarbeit.simplifx.event.EventEmitterImpl;
import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.internaldi.InjectorConfig;

public final class DIConfig extends InjectorConfig {

    private final IEventEmitter emitter;

    public DIConfig() {
        this.emitter = new EventEmitterImpl();
    }

    @Override
    protected void setup() {
        super.installInstanceBinding(IEventEmitter.class, this.emitter);
        super.installBinding(Application.class, ApplicationImpl.class);
    }

}
