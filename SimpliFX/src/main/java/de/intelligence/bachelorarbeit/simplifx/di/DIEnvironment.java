package de.intelligence.bachelorarbeit.simplifx.di;

/**
 * An interface for dependency injection without knowing the underlying dependency injection framework.
 */
public interface DIEnvironment {

    /**
     * Injects dependencies into the specified {@link Object}.
     *
     * @param obj The {@link Object} in which the dependencies should be injected.
     */
    void inject(Object obj);

    /**
     * Retrieves an instance of the specified class from the underlying dependency injection framework.
     *
     * @param clazz The class of which an instance is requested.
     * @param <T>   The class type.
     * @return An instance of the specified class from the underlying dependency injection framework.
     */
    <T> T get(Class<T> clazz);

}
