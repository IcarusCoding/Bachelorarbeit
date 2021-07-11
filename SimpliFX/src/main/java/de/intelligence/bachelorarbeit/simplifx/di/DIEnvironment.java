package de.intelligence.bachelorarbeit.simplifx.di;

public interface DIEnvironment {

    void inject(Object obj);

    <T> T get(Class<T> clazz);

}
