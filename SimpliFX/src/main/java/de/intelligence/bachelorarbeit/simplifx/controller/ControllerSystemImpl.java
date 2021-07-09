package de.intelligence.bachelorarbeit.simplifx.controller;

import java.net.URL;
import java.util.Optional;

import javafx.css.Stylesheet;

import com.sun.javafx.css.StyleManager;

import de.intelligence.bachelorarbeit.reflectionutils.ClassReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.logging.SimpliFXLogger;

public final class ControllerSystemImpl implements IControllerSystem {

    private static final SimpliFXLogger LOG = SimpliFXLogger.create(ControllerSystemImpl.class);

    @Override
    public boolean validateController(Class<?> clazz) {
        final ClassReflection clazzRef = Reflection.reflect(clazz);
        final Optional<Controller> controllerOpt = clazzRef.getAnnotation(Controller.class);
        if (controllerOpt.isEmpty()) {
            LOG.error("Controller \"" + clazz.getSimpleName() + "\" is not annotated with @Controller.");
            return false;
        }
        if (clazzRef.isNonStaticMember()) {
            LOG.error("Controller \"" + clazz.getSimpleName() + "\" must not be a non static member. Maybe add the static modifier?");
            return false;
        }
        final Controller annotation = controllerOpt.get();
        final String fxmlPath = annotation.fxml();
        URL fxmlLocation = null;
        if (fxmlPath.isBlank() || (fxmlLocation = clazz.getResource(fxmlPath)) == null) {
            LOG.error("Could not resolve fxml path (\"" + fxmlPath + "\") for controller \"" + clazz.getSimpleName() + "\".");
            return false;
        }
        URL cssLocation = null;
        final String cssPath = annotation.css();
        Stylesheet sheet = null; //TODO maybe remove check to avoid loading every css file 2x
        if (!cssPath.isBlank() && (sheet = StyleManager.loadStylesheet(cssPath)) == null) {
            LOG.warn("Invalid stylesheet (\"" + cssPath + "\") found for controller \"" + clazz.getSimpleName() + "\".");
        }
        return true;
    }

}
