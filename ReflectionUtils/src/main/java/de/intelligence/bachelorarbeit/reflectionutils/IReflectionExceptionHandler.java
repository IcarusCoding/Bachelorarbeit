package de.intelligence.bachelorarbeit.reflectionutils;

@FunctionalInterface
public interface IReflectionExceptionHandler {

    IReflectionExceptionHandler DEFAULT = Throwable::printStackTrace;

    void handleException(Exception exception);

}
