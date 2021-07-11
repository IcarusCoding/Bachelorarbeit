package de.intelligence.bachelorarbeit.simplifx.classpath.filter;

@FunctionalInterface
public interface IDiscoveryFilter<T> {

    boolean matches(T t);

}
