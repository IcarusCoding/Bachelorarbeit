package de.intelligence.bachelorarbeit.simplifx.application;

import javafx.application.Application;
import javafx.application.Preloader;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import de.intelligence.bachelorarbeit.simplifx.event.EventEmitterImpl;
import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.fxml.SimpliFXMLLoader;

//TODO maybe use own library
public final class DIConfig extends AbstractModule {

    private final IEventEmitter applicationEmitter;
    private final IEventEmitter preloaderEmitter;
    private final SimpliFXMLLoader loader;

    public DIConfig() {
        this.applicationEmitter = new EventEmitterImpl();
        this.preloaderEmitter = new EventEmitterImpl();
        this.loader = new SimpliFXMLLoader();
    }

    @Override
    protected void configure() {
        super.bind(IEventEmitter.class).annotatedWith(Names.named("applicationEmitter"))
                .toInstance(this.applicationEmitter);
        super.bind(IEventEmitter.class).annotatedWith(Names.named("preloaderEmitter"))
                .toInstance(this.preloaderEmitter);
        super.bind(SimpliFXMLLoader.class).toInstance(this.loader);
        super.bind(Application.class).to(ApplicationImpl.class);
        super.bind(Preloader.class).to(PreloaderImpl.class);
    }

}
