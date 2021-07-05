package de.intelligence.bachelorarbeit.simplifx.dagger1;

import java.util.Arrays;
import java.util.Optional;

import dagger.Module;

import de.intelligence.bachelorarbeit.reflectionutils.ConstructorReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;
import de.intelligence.bachelorarbeit.simplifx.di.IDIEnvironmentFactory;
import de.intelligence.bachelorarbeit.simplifx.logging.SimpliFXLogger;

public final class Dagger1EnvironmentFactory implements IDIEnvironmentFactory<Dagger1Injection> {

    private static final SimpliFXLogger LOG = SimpliFXLogger.create(Dagger1EnvironmentFactory.class);

    @Override
    public DIEnvironment create(Object obj, Dagger1Injection dagger1Injection) {
        final Object[] modules = Arrays.stream(dagger1Injection.value()).distinct().map(Reflection::reflect)
                .filter(classRef -> {
                    boolean annotationPresent = classRef.isAnnotationPresent(Module.class);
                    if (!annotationPresent) {
                        LOG.warn("Found invalid dagger module: " + classRef.getReflectable().getSimpleName() + ".");
                    }
                    return annotationPresent;
                })
                .map(classRef -> {
                    final Optional<ConstructorReflection> conRefOpt = classRef.hasConstructor();
                    if (conRefOpt.isEmpty()) {
                        LOG.warn("Could not create an instance of module " + classRef.getReflectable().getSimpleName()
                                + ". Reason: Missing default constructor.");
                    }
                    return conRefOpt;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(conRef -> conRef.forceAccess().instantiate().getReflectable())
                .toArray();
        return new Dagger1Environment(obj, modules);
    }

}
