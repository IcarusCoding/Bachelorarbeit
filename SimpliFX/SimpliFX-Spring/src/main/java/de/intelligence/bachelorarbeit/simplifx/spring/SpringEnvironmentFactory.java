package de.intelligence.bachelorarbeit.simplifx.spring;

import java.util.Arrays;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;
import de.intelligence.bachelorarbeit.simplifx.di.IDIEnvironmentFactory;

public final class SpringEnvironmentFactory implements IDIEnvironmentFactory<SpringInjection> {

    @Override
    public DIEnvironment create(Object obj, SpringInjection springInjection) {
        return new SpringEnvironment(obj, Arrays.stream(springInjection.value()).distinct().toArray(Class[]::new));
    }

}
