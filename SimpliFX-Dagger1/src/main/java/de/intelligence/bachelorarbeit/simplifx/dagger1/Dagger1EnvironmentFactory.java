package de.intelligence.bachelorarbeit.simplifx.dagger1;

import java.util.Arrays;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.Module;

import de.intelligence.bachelorarbeit.reflectionutils.ConstructorReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;
import de.intelligence.bachelorarbeit.simplifx.di.IDIEnvironmentFactory;

public final class Dagger1EnvironmentFactory implements IDIEnvironmentFactory<Dagger1Injection> {

    private static final Logger LOG = LogManager.getLogger(Dagger1EnvironmentFactory.class);

    @Override
    public DIEnvironment create(Dagger1Injection dagger1Injection) {
        final Object[] modules = Arrays.stream(dagger1Injection.value()).distinct().map(Reflection::reflect)
                .filter(classRef -> {
                    boolean annotationPresent = classRef.isAnnotationPresent(Module.class);
                    if (!annotationPresent) {
                        LOG.warn("Found invalid dagger module: {}.", classRef.getReflectable().getSimpleName());
                    }
                    return annotationPresent;
                })
                .map(classRef -> {
                    final Optional<ConstructorReflection> conRefOpt = classRef.hasConstructor();
                    if (conRefOpt.isEmpty()) {
                        LOG.warn("Could not create an instance of module {}. Reason: Missing default constructor.",
                                classRef.getReflectable().getSimpleName());
                    }
                    return conRefOpt;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(conRef -> conRef.forceAccess().instantiate().getReflectable())
                .toArray();
        return new Dagger1Environment(modules);
    }

}
