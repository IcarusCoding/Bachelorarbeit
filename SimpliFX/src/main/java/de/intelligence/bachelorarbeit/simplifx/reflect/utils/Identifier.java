package de.intelligence.bachelorarbeit.simplifx.reflect.utils;

@FunctionalInterface
public interface Identifier<T> {

    boolean check(T t);

}
