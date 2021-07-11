package de.intelligence.bachelorarbeit.simplifx.controller;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

import javafx.css.Stylesheet;
import javafx.scene.layout.Pane;

import com.sun.javafx.css.StyleManager;

import org.xml.sax.SAXException;

import de.intelligence.bachelorarbeit.reflectionutils.ClassReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.exception.InvalidControllerDefinitionException;
import de.intelligence.bachelorarbeit.simplifx.fxml.SimpliFXMLLoader;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.logging.SimpliFXLogger;
import de.intelligence.bachelorarbeit.simplifx.utils.AnnotationUtils;

final class ControllerCreator {

    private static final SimpliFXLogger LOG = SimpliFXLogger.create(DefaultControllerGroup.class);

    private final IControllerFactoryProvider provider;
    private final II18N ii18N;

    ControllerCreator(IControllerFactoryProvider provider, II18N ii18N) {
        this.provider = provider;
        this.ii18N = ii18N;
    }

    IController createController(Class<?> clazz) throws IOException {
        final ControllerLoadContext ctx = this.validateController(clazz);
        boolean validControllerAttribSpecified = false;

        try (InputStream input = ctx.fxmlLocation.openStream()) {
            final String attrib = XPathFactory.newInstance().newXPath().compile("/*/@*[local-name()='controller']")
                    .evaluate(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input));
            if (!attrib.isBlank() && clazz.getCanonicalName().equals(attrib)) {
                validControllerAttribSpecified = true;
            }
        } catch (ParserConfigurationException | SAXException | XPathExpressionException ignored) {
        }

        final SimpliFXMLLoader loader = new SimpliFXMLLoader();
        if (validControllerAttribSpecified) {
            loader.setControllerFactory(this.provider.provide());
        } else {
            loader.setController(this.provider.create(clazz));
        }
        loader.setII18N(this.ii18N);
        loader.setClassLoader(clazz.getClassLoader());
        loader.setLocation(ctx.fxmlLocation);
        final Pane pane = loader.load();
        final Object instance = loader.getController();
        pane.getStylesheets().add(ctx.cssLocation);
        AnnotationUtils.invokeMethodsByPrioritizedAnnotation(instance,
                PostConstruct.class, m -> m.getParameterCount() == 0, PostConstruct::value);
        return new ControllerImpl(instance, pane);
    }

    private ControllerLoadContext validateController(Class<?> clazz) {
        final ClassReflection clazzRef = Reflection.reflect(clazz);
        final Optional<Controller> controllerOpt = clazzRef.getAnnotation(Controller.class);
        if (controllerOpt.isEmpty()) {
            throw new InvalidControllerDefinitionException("Controller \"" + clazz.getSimpleName() + "\" is not annotated with @Controller.");
        }
        if (clazzRef.isNonStaticMember()) {
            throw new InvalidControllerDefinitionException("Controller \"" + clazz.getSimpleName() + "\" must not be a non static member. Maybe add the static modifier?");
        }
        final Controller annotation = controllerOpt.get();
        final String fxmlPath = annotation.fxml();
        URL fxmlLocation = null;
        if (fxmlPath.isBlank() || (fxmlLocation = clazz.getResource(fxmlPath)) == null) {
            throw new InvalidControllerDefinitionException("Could not resolve fxml path (\"" + fxmlPath + "\") for controller \"" + clazz.getSimpleName() + "\".");
        }
        final String cssPath = annotation.css();
        Stylesheet sheet = null; //TODO maybe remove check to avoid loading every css file 2x
        if (!cssPath.isBlank() && (sheet = StyleManager.loadStylesheet(cssPath)) == null) {
            LOG.warn("Invalid stylesheet (\"" + cssPath + "\") found for controller \"" + clazz.getSimpleName() + "\".");
        }
        return new ControllerLoadContext(fxmlLocation, annotation.css());
    }

    private static final record ControllerLoadContext(URL fxmlLocation, String cssLocation) {
    }

}
