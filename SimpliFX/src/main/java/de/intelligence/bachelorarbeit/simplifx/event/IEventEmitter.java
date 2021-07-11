package de.intelligence.bachelorarbeit.simplifx.event;

public interface IEventEmitter {

    void emit(Object obj);

    void register(Object obj);

    void unregister(Object obj);

}
