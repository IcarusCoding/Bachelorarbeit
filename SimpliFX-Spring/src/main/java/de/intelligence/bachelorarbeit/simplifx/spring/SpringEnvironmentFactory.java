package de.intelligence.bachelorarbeit.simplifx.spring;

import java.util.Arrays;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;
import de.intelligence.bachelorarbeit.simplifx.di.IDIEnvironmentFactory;

/**
 * An implementation of the {@link IDIEnvironmentFactory} to create an dependency injection environment for the
 * spring framework.
 */
public final class SpringEnvironmentFactory implements IDIEnvironmentFactory<SpringInjection> {

    @Override
    public DIEnvironment create(SpringInjection springInjection) {
        return new SpringEnvironment(Arrays.stream(springInjection.value()).distinct().toArray(Class[]::new));
    }

}
