package de.intelligence.bachelorarbeit.simplifx.guice;

import java.util.Arrays;
import java.util.Optional;

import com.google.inject.Module;

import de.intelligence.bachelorarbeit.reflectionutils.ConstructorReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;
import de.intelligence.bachelorarbeit.simplifx.di.IDIEnvironmentFactory;
import de.intelligence.bachelorarbeit.simplifx.logging.SimpliFXLogger;

public final class GuiceEnvironmentFactory implements IDIEnvironmentFactory<GuiceInjection> {

    private static final SimpliFXLogger LOG = SimpliFXLogger.create(GuiceEnvironmentFactory.class);

    @Override
    public DIEnvironment create(Object root, GuiceInjection guiceInjection) {
        final Module[] modules = Arrays.stream(guiceInjection.value()).distinct().map(Reflection::reflect)
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
                .map(Module.class::cast).toArray(Module[]::new);
        return new GuiceEnvironment(root, modules);
    }

}
