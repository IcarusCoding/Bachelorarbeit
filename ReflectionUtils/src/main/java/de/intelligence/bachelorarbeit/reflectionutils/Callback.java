package de.intelligence.bachelorarbeit.reflectionutils;

/**
 * A basic callback interface
 *
 * @author Deniz Groenhoff
 */
@FunctionalInterface
public interface Callback<T> {

    void callback(T t);

}
