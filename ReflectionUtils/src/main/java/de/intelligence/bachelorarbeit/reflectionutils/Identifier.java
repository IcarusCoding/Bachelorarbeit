package de.intelligence.bachelorarbeit.reflectionutils;

/**
 * A basic filter interface
 *
 * @author Deniz Groenhoff
 */
@FunctionalInterface
public interface Identifier<T> {

    boolean check(T t);

}
