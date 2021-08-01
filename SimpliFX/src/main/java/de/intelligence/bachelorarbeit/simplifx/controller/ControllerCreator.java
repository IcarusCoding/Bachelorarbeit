package de.intelligence.bachelorarbeit.simplifx.controller;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;

import javafx.scene.layout.Pane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import de.intelligence.bachelorarbeit.reflectionutils.ClassReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.config.ConfigValueInjector;
import de.intelligence.bachelorarbeit.simplifx.config.PropertyRegistry;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.exception.ControllerConstructionException;
import de.intelligence.bachelorarbeit.simplifx.exception.InvalidControllerDefinitionException;
import de.intelligence.bachelorarbeit.simplifx.fxml.SimpliFXMLLoader;
import de.intelligence.bachelorarbeit.simplifx.injection.AnnotatedFieldDetector;
import de.intelligence.bachelorarbeit.simplifx.injection.IAnnotatedFieldDetector;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedFieldInjector;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedResources;
import de.intelligence.bachelorarbeit.simplifx.utils.Prefix;

public final class ControllerCreator {

    private static final Logger LOG = LogManager.getLogger(ControllerGroupImpl.class);

    private final IControllerFactoryProvider provider;
    private final II18N ii18N;
    private final SharedResources resources;
    private final PropertyRegistry registry;

    public ControllerCreator(IControllerFactoryProvider provider, II18N ii18N, SharedResources resources, PropertyRegistry registry) {
        this.provider = provider;
        this.ii18N = ii18N;
        this.resources = resources;
        this.registry = registry;
    }

    public IController createController(Class<?> clazz) {
        final ControllerLoadContext ctx = this.validateController(clazz);
        boolean validControllerAttribSpecified = false;

        try (InputStream input = ctx.fxmlLocation.openStream()) {
            final String attrib = XPathFactory.newInstance().newXPath().compile("/*/@*[local-name()='controller']")
                    .evaluate(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input));
            if (!attrib.isBlank() && clazz.getCanonicalName().equals(attrib)) {
                validControllerAttribSpecified = true;
            }
        } catch (ParserConfigurationException | SAXException | XPathExpressionException | IOException ignored) {
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
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException ex) {
            throw new ControllerConstructionException("Failed to load FXML file \"" + ctx.fxmlLocation + "\" for controller \"" + clazz.getSimpleName() + "\".", ex);
        }
        final Object instance = loader.getController();
        if (ctx.validCSS) {
            pane.getStylesheets().add(ctx.cssLocation);
        }
        final IAnnotatedFieldDetector<ResourceBundle> detector = new AnnotatedFieldDetector<>(ResourceBundle.class, instance);
        detector.findAllFields();
        detector.injectValue(this.ii18N, true, (f, ex) -> {
            throw new ControllerConstructionException("Failed to inject resources into field " + f, ex);
        });
        new ConfigValueInjector(instance).inject(this.registry);
        new SharedFieldInjector(instance).inject(this.resources);
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
        if (fxmlPath.isBlank() || !fxmlPath.toLowerCase(Locale.ROOT).endsWith(".fxml") || (fxmlLocation = clazz.getResource(fxmlPath)) == null) {
            throw new InvalidControllerDefinitionException("Could not resolve fxml path (\"" + fxmlPath + "\") for controller \"" + clazz.getSimpleName() + "\".");
        }
        String cssPath = annotation.css().isBlank() ? "" : (annotation.css().startsWith(Prefix.FILE_SEPARATOR) ? annotation.css() : Prefix.FILE_SEPARATOR + annotation.css());
        boolean validCSS = true;
        if (!cssPath.isBlank() && clazz.getResource(cssPath) == null) {
            validCSS = false;
            LOG.warn("Invalid stylesheet (\"" + cssPath + "\") found for controller \"" + clazz.getSimpleName() + "\".");
        }
        return new ControllerLoadContext(fxmlLocation, annotation.css(), validCSS);
    }

    private static final record ControllerLoadContext(URL fxmlLocation, String cssLocation, boolean validCSS) {
    }

}
