package de.intelligence.bachelorarbeit.simplifx.application;

import javafx.application.Application;

import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.internaldi.InjectorConfig;

public final class DIConfig extends InjectorConfig {

    @Override
    protected void setup() {
        super.installFactory(IEventEmitter.class, () -> new IEventEmitter() {
            @Override
            public void emit(Object obj) {
                System.out.println("Emitting: " + obj);
            }

            @Override
            public void register(Object obj) {

            }
        });
        super.installBinding(Application.class, ApplicationImpl.class);
    }

}
