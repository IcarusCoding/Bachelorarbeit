package de.intelligence.bachelorarbeit.simplifx;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.sun.javafx.application.ParametersImpl;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.stage.StageHelper;
import com.sun.javafx.util.Logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.intelligence.bachelorarbeit.reflectionutils.ClassReflection;
import de.intelligence.bachelorarbeit.reflectionutils.ConstructorReflection;
import de.intelligence.bachelorarbeit.reflectionutils.FieldReflection;
import de.intelligence.bachelorarbeit.reflectionutils.MethodReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.application.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.application.ApplicationFactory;
import de.intelligence.bachelorarbeit.simplifx.application.DIConfig;
import de.intelligence.bachelorarbeit.simplifx.application.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.application.PreloaderEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.application.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.classpath.ClassDiscovery;
import de.intelligence.bachelorarbeit.simplifx.classpath.DiscoveryContextBuilder;
import de.intelligence.bachelorarbeit.simplifx.classpath.IDiscoveryResult;
import de.intelligence.bachelorarbeit.simplifx.config.ConfigSource;
import de.intelligence.bachelorarbeit.simplifx.config.ConfigValueInjector;
import de.intelligence.bachelorarbeit.simplifx.config.PropertyRegistry;
import de.intelligence.bachelorarbeit.simplifx.controller.ControllerGroupImpl;
import de.intelligence.bachelorarbeit.simplifx.controller.IControllerGroup;
import de.intelligence.bachelorarbeit.simplifx.controller.INotificationDialog;
import de.intelligence.bachelorarbeit.simplifx.controller.NotificationDialog;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.DIControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.FXMLControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.controller.provider.IControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.di.DIAnnotation;
import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;
import de.intelligence.bachelorarbeit.simplifx.di.IDIEnvironmentFactory;
import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.exception.ApplicationConstructionException;
import de.intelligence.bachelorarbeit.simplifx.exception.SimpliFXException;
import de.intelligence.bachelorarbeit.simplifx.experimental.SubclassControllerFactoryProvider;
import de.intelligence.bachelorarbeit.simplifx.experimental.SubclassingClassLoader;
import de.intelligence.bachelorarbeit.simplifx.injection.AnnotatedFieldDetector;
import de.intelligence.bachelorarbeit.simplifx.injection.IAnnotatedFieldDetector;
import de.intelligence.bachelorarbeit.simplifx.localization.CompoundResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.localization.I18N;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.localization.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedFieldInjector;
import de.intelligence.bachelorarbeit.simplifx.shared.SharedResources;
import de.intelligence.bachelorarbeit.simplifx.utils.AnnotationUtils;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
import de.intelligence.bachelorarbeit.simplifx.utils.Pair;

/**
 * A class which is responsible for all SimpliFX related operations.
 *
 * @author Deniz Groenhoff
 */
public final class SimpliFX {

    private static final Logger LOG = LogManager.getLogger(SimpliFX.class);

    private static IDiscoveryResult discoveryResult;
    private static ClasspathScanPolicy scanPolicy = ClasspathScanPolicy.LOCAL;
    private static Class<?> callerClass;
    private static Injector globalInjector;
    private static II18N globalI18N;
    private static DIEnvironment appDIEnv;
    private static SharedResources globalResources;
    private static PropertyRegistry globalPropertyRegistry;
    private static Function<Pane, INotificationDialog> defaultNotificationHandler = NotificationDialog::new;
    private static Consumer<Throwable> exceptionHandler;
    private static boolean experimentalEnabled;
    private static boolean launched;

    private SimpliFX() {
        throw new UnsupportedOperationException();
    }

    /**
     * Enables experimental features.
     */
    public static void enableExperimentalFeatures() {
        SimpliFX.experimentalEnabled = true;
    }

    /**
     * Sets a new policy for class path scanning.
     *
     * @param scanPolicy The new policy for class path scanning.
     */
    public static void setClasspathScanPolicy(ClasspathScanPolicy scanPolicy) {
        if (SimpliFX.launched) {
            throw new SimpliFXException("Cannot set the classpath scan policy after the application was launched");
        }
        SimpliFX.scanPolicy = scanPolicy;
    }

    /**
     * Sets the default notification handler for controller group notifications. This will be used for creating the
     * main controller group and in every created subgroup if no other handler is specified during the creation process.
     *
     * @param defaultNotificationHandler The new default notification handler.
     */
    public static void setDefaultNotificationHandler(Function<Pane, INotificationDialog> defaultNotificationHandler) {
        if (SimpliFX.launched) {
            throw new SimpliFXException("Cannot set the default notification handler after the application was launched");
        }
        SimpliFX.defaultNotificationHandler = defaultNotificationHandler;
    }

    /**
     * Sets a handler which will be used if exceptions are thrown during the application creation process or exceptions
     * which results in an immediate termination of SimpliFX.
     *
     * @param exceptionHandler The new exception handler.
     */
    public static void setExceptionHandler(Consumer<Throwable> exceptionHandler) {
        if (SimpliFX.launched) {
            throw new SimpliFXException("Cannot set the exception handler after the application was launched");
        }
        SimpliFX.exceptionHandler = exceptionHandler;
    }

    /**
     * Launches the application with the specified arguments.
     * The entrypoint will be found by scanning the classpath.
     *
     * @param args The arguments which will be used in the application creation process.
     */
    public static void launch(String... args) {
        final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        SimpliFX.callerClass = walker.getCallerClass();
        SimpliFX.startDiscovery();
        SimpliFX.launch(SimpliFX.findApplicationClass(), args);
    }

    /**
     * Launches the application with the specified arguments and a preloader.
     * The entrypoints for the application and the preloader will be found by scanning the classpath.
     *
     * @param args The arguments which will be used in the application creation process.
     */
    public static void launchWithPreloader(String... args) {
        final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        SimpliFX.callerClass = walker.getCallerClass();
        SimpliFX.startDiscovery();
        SimpliFX.launch(SimpliFX.findApplicationClass(), SimpliFX.findPreloaderClass(), args);
    }

    /**
     * Launches the application with the specified arguments and the entrypoint class.
     *
     * @param appEntrypointClass The entrypoint class.
     * @param args               The arguments which will be used in the application creation process.
     */
    public static void launch(Class<?> appEntrypointClass, String... args) {
        SimpliFX.checkLaunch();
        SimpliFX.launchImpl(SimpliFX.validateEntrypointClass(appEntrypointClass, ApplicationEntryPoint.class)
                .forceAccess().instantiate().getReflectable(), null, args);
    }

    /**
     * Launches the application with the specified arguments and the entrypoint classes for the application and the preloader.
     *
     * @param applicationClass The entrypoint class.
     * @param preloaderClass   The preloader class.
     * @param args             The arguments which will be used in the application creation process.
     */
    public static void launch(Class<?> applicationClass, Class<?> preloaderClass, String... args) {
        Conditions.checkCondition(applicationClass != preloaderClass,
                "Different classes needed for application and preloader entrypoint!");
        SimpliFX.checkLaunch();
        SimpliFX.launchImpl(SimpliFX.validateEntrypointClass(applicationClass, ApplicationEntryPoint.class)
                        .forceAccess().instantiate().getReflectable(),
                SimpliFX.validateEntrypointClass(preloaderClass, PreloaderEntryPoint.class)
                        .forceAccess().instantiate().getReflectable(), args);
    }

    /**
     * Launches the application with the specified arguments and the application entrypoint instance.
     *
     * @param applicationListener The instance of the application entrypoint.
     * @param args                The arguments which will be used in the application creation process.
     */
    public static void launch(Object applicationListener, String... args) {
        SimpliFX.validateEntrypointInstance(applicationListener, ApplicationEntryPoint.class);
        SimpliFX.checkLaunch();
        SimpliFX.launchImpl(applicationListener, null, args);
    }

    /**
     * Launches the application with the specified arguments and the entrypoint for the application and the preloader.
     *
     * @param applicationListener The instance of the application entrypoint.
     * @param preloaderListener   The instance of the preloader entrypoint.
     * @param args                The arguments which will be used in the application creation process.
     */
    public static void launch(Object applicationListener, Object preloaderListener, String... args) {
        Conditions.checkCondition(applicationListener.getClass() != preloaderListener.getClass(),
                "Different classes needed for application and preloader entrypoint!");
        SimpliFX.validateEntrypointInstance(applicationListener, ApplicationEntryPoint.class);
        SimpliFX.validateEntrypointInstance(preloaderListener, PreloaderEntryPoint.class);
        SimpliFX.checkLaunch();
        SimpliFX.launchImpl(applicationListener, preloaderListener, args);
    }

    private static Class<?> findApplicationClass() {
        Class<?> applicationClass = null;
        if (SimpliFX.validateEntryPoint(callerClass, ApplicationEntryPoint.class)) {
            applicationClass = callerClass;
        } else {
            final List<Class<?>> declared = SimpliFX
                    .findDeclaredClassesAnnotatedBy(callerClass, ApplicationEntryPoint.class);
            if (!declared.isEmpty()) {
                applicationClass = declared.get(0);
            }
        }
        if (applicationClass == null) {
            final Optional<Class<?>> clazzOpt = SimpliFX.discoveryResult
                    .findClassesAnnotatedBy(ApplicationEntryPoint.class).stream()
                    .filter(SimpliFX::validateClassType).findFirst();
            if (clazzOpt.isPresent()) {
                return clazzOpt.get();
            }
            throw new IllegalStateException("Could not find application class!");
        }
        return applicationClass;
    }

    private static Class<?> findPreloaderClass() {
        final Optional<Class<?>> clazzOpt = SimpliFX.discoveryResult.findClassesAnnotatedBy(PreloaderEntryPoint.class)
                .stream().filter(SimpliFX::validateClassType).findFirst();
        if (clazzOpt.isPresent()) {
            return clazzOpt.get();
        }
        throw new IllegalStateException("Could not find preloader class!");
    }

    private static void checkLaunch() {
        if (SimpliFX.launched) {
            throw new SimpliFXException("Cannot start application more than once!");
        }
        SimpliFX.launched = true;
    }

    private static void launchImpl(Object applicationListener, Object preloaderListener, String... args) {
        Logging.getJavaFXLogger().disableLogging();
        Logging.getCSSLogger().disableLogging();
        SimpliFX.globalInjector = Guice.createInjector(new DIConfig());
        SimpliFX.globalResources = new SharedResources();
        SimpliFX.globalPropertyRegistry = new PropertyRegistry(applicationListener);
        final Class<?> applicationClass = applicationListener.getClass();

        System.out.println(new String(SimpliFXConstants.BANNER, StandardCharsets.UTF_8));
        SimpliFX.LOG.info("Starting application with entrypoint: {}.", applicationClass.getName());

        final Preloader preImpl = preloaderListener == null ? null : SimpliFX.globalInjector.getInstance(Preloader.class);
        final Application appImpl = SimpliFX.globalInjector.getInstance(ApplicationFactory.class).create(preImpl);

        // I18N setup
        final AnnotatedFieldDetector<ResourceBundle> bundleDetector = new AnnotatedFieldDetector<>(ResourceBundle.class,
                applicationListener, preloaderListener);
        SimpliFX.globalI18N = SimpliFX.setupI18N(bundleDetector);
        bundleDetector.injectValue(SimpliFX.globalI18N, true, (f, ex) -> {
            throw new ApplicationConstructionException("Failed to inject resources into field " + f.getName(), ex);
        });

        // Configuration source setup
        final AnnotatedFieldDetector<ConfigSource> configDetector = new AnnotatedFieldDetector<>(ConfigSource.class,
                applicationListener, preloaderListener);
        configDetector.findAllFields((fRef, a) -> fRef.getReflectable().getType().equals(Properties.class));
        configDetector.getAnnotations().stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(ConfigSource::value))), HashSet::new)).forEach(source -> {
            if (source.source() == ConfigSource.Source.CLASSPATH) {
                SimpliFX.globalPropertyRegistry.loadFromClasspath(source.value());
                return;
            }
            SimpliFX.globalPropertyRegistry.loadFromOutside(source.value());
        });
        configDetector.injectValue(SimpliFX.globalPropertyRegistry.getReadOnlyProperties(), true, (f, ex) -> {
            throw new ApplicationConstructionException("Failed to inject properties into field " + f.getName(), ex);
        });

        // Configuration value setup
        new ConfigValueInjector(applicationListener, preloaderListener).inject(SimpliFX.globalPropertyRegistry);

        // SharedResources setup
        new SharedFieldInjector(applicationListener, preloaderListener).inject(SimpliFX.globalResources);

        // DI setup
        try {
            SimpliFX.setupDI(applicationListener);
        } catch (Exception ex) {
            throw new ApplicationConstructionException("There was an error while trying to setup the DI framework: ", ex);
        }

        // PostConstruct invocation
        if (preloaderListener != null) {
            AnnotationUtils.invokeMethodsByAnnotation(preloaderListener,
                    PostConstruct.class, PostConstruct::value, true);
        }
        AnnotationUtils.invokeMethodsByAnnotation(applicationListener,
                PostConstruct.class, PostConstruct::value, true);

        final AtomicReference<RuntimeException> launcherException = new AtomicReference<>();
        final Thread fxLauncherThread = new Thread(() -> {
            final Launcher l = new Launcher(appImpl, preImpl);
            try {
                l.launchApplication(applicationListener, preloaderListener, args);
            } catch (Exception ex) {
                launcherException.set(new RuntimeException("Launcher exception", ex));
            }
        }, "SimpliFX Launcher Thread");
        fxLauncherThread.start();
        try {
            fxLauncherThread.join();
        } catch (InterruptedException ex) {
            final RuntimeException unexpected = new RuntimeException("Unexpected exception thrown: ", ex);
            if (SimpliFX.exceptionHandler == null) {
                throw unexpected;
            }
            SimpliFX.exceptionHandler.accept(unexpected);
        }
        if (launcherException.get() != null) {
            if (SimpliFX.exceptionHandler == null) {
                throw launcherException.get();
            }
            SimpliFX.exceptionHandler.accept(launcherException.get());
        }
    }

    private static II18N setupI18N(IAnnotatedFieldDetector<ResourceBundle> bundleDetector) {
        bundleDetector.findAllFields((f, a) -> f.canAccept(I18N.class));
        final Map<Locale, List<java.util.ResourceBundle>> bundleMap = new HashMap<>();
        bundleDetector.getAnnotations()
                .stream().flatMap(b -> Arrays.stream(b.value())).filter(b -> !b.isBlank()).forEach(b -> Arrays.stream(Locale.getAvailableLocales())
                .map(locale -> java.util.ResourceBundle.getBundle(b, locale))
                .filter(Conditions.distinct(java.util.ResourceBundle::getLocale)).forEach(bundle -> {
                    if (bundle.getLocale().getLanguage().isBlank()) {
                        // filter default
                        return;
                    }
                    if (!bundleMap.containsKey(bundle.getLocale())) {
                        bundleMap.put(bundle.getLocale(), new ArrayList<>());
                    }
                    bundleMap.get(bundle.getLocale()).add(bundle);
                }));
        return new I18N(bundleMap.entrySet().stream().map(entry ->
                new CompoundResourceBundle(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
    }

    private static void setupDI(Object applicationListener) {
        final Annotation[] annotations = applicationListener.getClass().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(DIAnnotation.class)) {
                final Class<? extends IDIEnvironmentFactory<? extends Annotation>> factory = annotation.annotationType()
                        .getAnnotation(DIAnnotation.class).value();
                final ClassReflection classRef = Reflection.reflect(factory);
                final Optional<ConstructorReflection> constructorRefOpt = classRef.hasConstructor();
                if (constructorRefOpt.isEmpty()) {
                    LOG.warn("Failed to instantiate DI factory {}. Reason: Missing default constructor.",
                            classRef.getReflectable().getSimpleName());
                    break;
                }
                final IDIEnvironmentFactory<?> factoryInstance = constructorRefOpt.get().instantiateUnsafeAndGet();
                final MethodReflection methodRef = Reflection.reflect(factoryInstance)
                        .reflectMethod("create", (Class<?>) ((ParameterizedType) factory.getGenericInterfaces()[0])
                                .getActualTypeArguments()[0]);
                SimpliFX.appDIEnv = methodRef.invokeUnsafe(annotation);
                SimpliFX.appDIEnv.inject(applicationListener);
                break;
            }
        }
    }

    private static void startDiscovery() {
        if (SimpliFX.discoveryResult == null) {
            String scanPath = "";
            if (SimpliFX.scanPolicy == ClasspathScanPolicy.LOCAL) {
                final String packageName = SimpliFX.callerClass.getPackageName();
                scanPath = packageName.substring(0, packageName.indexOf('.'));
            }
            SimpliFX.discoveryResult = new ClassDiscovery(new DiscoveryContextBuilder().setDefaultClassLoaders()
                    .setPath(scanPath).build()).startDiscovery();
        }
    }

    private static List<Class<?>> findDeclaredClassesAnnotatedBy(Class<?> clazz,
                                                                 Class<? extends Annotation> annotation) {
        final List<Class<?>> validClasses = new ArrayList<>();
        for (Class<?> declaredClass : clazz.getDeclaredClasses()) {
            if (SimpliFX.validateEntryPoint(declaredClass, annotation)) {
                validClasses.add(declaredClass);
            }
        }
        return validClasses;
    }

    private static boolean validateClassType(Class<?> clazz) {
        return (Modifier.isStatic(clazz.getModifiers()) || clazz.getEnclosingClass() == null)
                && !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isEnum();
    }

    private static boolean validateEntryPoint(Class<?> clazz, Class<? extends Annotation> annotation) {
        return SimpliFX.validateClassType(clazz) && Reflection.reflect(clazz)
                .isAnnotationPresent(annotation);
    }

    private static ConstructorReflection validateEntrypointClass(Class<?> clazz,
                                                                 Class<? extends Annotation> annotation) {
        Conditions.checkNull(clazz, "Entrypoint class cannot be null!");
        Conditions.checkCondition(SimpliFX.validateEntryPoint(clazz, annotation),
                "Invalid entrypoint class specified: " + clazz.getSimpleName());
        final ConstructorReflection conRef = Conditions
                .nullOnException(() -> Reflection.reflect(clazz).findConstructor());
        return Conditions.checkNull(conRef, "Unable to locate default constructor!");
    }

    private static void validateEntrypointInstance(Object instance, Class<? extends Annotation> annotation) {
        Conditions.checkNull(instance, "Entrypoint instance cannot be null!");
        Conditions.checkCondition(SimpliFX.validateEntryPoint(instance.getClass(), annotation),
                "Invalid entrypoint instance specified: " + instance.getClass().getSimpleName());
    }

    private static final class Launcher {

        private static final AtomicBoolean toolkitInitialized = new AtomicBoolean(false);

        private final Application applicationImpl;
        private final Preloader preloaderImpl;

        private final Semaphore applicationExitSem;
        private final Semaphore preloaderExitSem;
        private final AtomicReference<LaunchState> currAppState;
        private final AtomicReference<LaunchState> currPreState;
        private final Map<String, Pair<Throwable, Boolean>> exceptions;

        private boolean errored;

        private <T extends Application, S extends Preloader> Launcher(T applicationImpl, S preloaderImpl) {
            this.applicationImpl = applicationImpl;
            this.preloaderImpl = preloaderImpl;
            this.applicationExitSem = new Semaphore(0);
            this.preloaderExitSem = new Semaphore(0);
            this.currAppState = new AtomicReference<>(LaunchState.INIT);
            this.currPreState = new AtomicReference<>(LaunchState.INIT);
            this.exceptions = new LinkedHashMap<>();
        }

        private static boolean wasVisible(Stage stage) {
            final ClassReflection classRef = Reflection.reflect(Window.class);
            final AtomicBoolean errored = new AtomicBoolean();
            classRef.setExceptionHandler(ex -> errored.set(true));
            final FieldReflection fieldRef = classRef.reflectField("hasBeenVisible");
            fieldRef.setAccessor(stage);
            final boolean shown = fieldRef.forceAccess().getUnsafe();
            if (errored.get()) {
                return false;
            }
            return shown;
        }

        private static Pair<StageConfig, Stage> createStage(Stage stage, Class<?> entrypointClass) {
            final Optional<StageConfig> configOpt = Reflection.reflect(entrypointClass).getAnnotation(StageConfig.class);
            if (configOpt.isEmpty()) {
                return Pair.of(null, stage);
            }
            final StageConfig config = configOpt.get();
            stage.setTitle(config.title());
            stage.initStyle(config.style());
            stage.setAlwaysOnTop(config.alwaysTop());
            boolean iconError = false;
            if (config.icons().length != 0) {
                for (String path : config.icons()) {
                    try (InputStream stream = entrypointClass.getResourceAsStream(path)) {
                        if (stream != null) {
                            stage.getIcons().add(new Image(stream));
                        } else {
                            iconError = true;
                        }
                    } catch (IOException ignored) {
                        iconError = true;
                    }
                    if (iconError) {
                        LOG.warn("Could not load icon {}.", path);
                        iconError = false;
                    }
                }
            }
            stage.setResizable(config.resizeable());
            return Pair.of(config, stage);
        }

        private void doNotifyProgress(double progress) {
            if (this.preloaderImpl == null) {
                return;
            }
            PlatformImpl.runAndWait(() -> this.preloaderImpl
                    .handleProgressNotification(new Preloader.ProgressNotification(progress)));
        }

        private void doNotifyStateChange(Preloader.StateChangeNotification.Type type) {
            if (this.preloaderImpl == null) {
                return;
            }
            PlatformImpl.runAndWait(() -> this.preloaderImpl
                    .handleStateChangeNotification(new Preloader.StateChangeNotification(type, this.applicationImpl)));
        }

        private boolean doNotifyError(String message, Throwable cause) {
            if (this.preloaderImpl == null) {
                return false;
            }
            final AtomicBoolean retRef = new AtomicBoolean();
            PlatformImpl.runAndWait(() -> retRef.set(this.preloaderImpl
                    .handleErrorNotification(new Preloader.ErrorNotification(null, message, cause))));
            return retRef.get();
        }

        private void launchApplication(Object applicationListener, Object preloaderListener, String[] args) throws
                Exception {
            SimpliFX.globalInjector.getInstance(Key.get(IEventEmitter.class, Names.named("applicationEmitter")))
                    .register(applicationListener);
            if (preloaderListener != null) {
                SimpliFX.globalInjector.getInstance(Key.get(IEventEmitter.class, Names.named("preloaderEmitter")))
                        .register(preloaderListener);
            }
            if (!Launcher.toolkitInitialized.getAndSet(true)) {
                final Semaphore sem = new Semaphore(0);
                PlatformImpl.startup(sem::release);
                sem.acquire();
            }
            PlatformImpl.runAndWait(() -> Thread.currentThread().setUncaughtExceptionHandler((t, th) -> {
                errored = true;
                this.exceptions.put("Exception on FX thread", Pair.of(th, false));
                Platform.exit();
            }));
            final ExitListener exitListener = new ExitListener();
            PlatformImpl.addListener(exitListener);
            try {
                if (preloaderListener != null) {
                    ParametersImpl.registerParameters(this.preloaderImpl, new ParametersImpl(args));
                    try {
                        this.preloaderImpl.init();
                    } catch (Exception ex) {
                        this.exceptions.put("Exception while handling preloader init event", Pair.of(ex, false));
                        this.errored = true;
                    }
                    if (!this.errored && currAppState.get() != LaunchState.EXIT) {
                        PlatformImpl.runAndWait(() -> {
                            this.currPreState.set(LaunchState.START);
                            final Pair<StageConfig, Stage> stageConfig = Launcher.createStage(new Stage(), preloaderListener.getClass());
                            final Stage primary = stageConfig.getRight();
                            StageHelper.setPrimary(primary, true);
                            try {
                                this.preloaderImpl.start(primary);
                                if (stageConfig.getLeft().autoShow() && !Launcher.wasVisible(primary)) {
                                    primary.show();
                                }
                            } catch (Exception ex) {
                                this.exceptions.put("Exception while handling preloader start event", Pair.of(ex, false));
                                this.errored = true;
                            }
                        });
                    }
                    if (!this.errored && currAppState.get() != LaunchState.EXIT) {
                        this.doNotifyProgress(0);
                    }
                }
                if (!errored && currAppState.get() != LaunchState.EXIT) {
                    this.doNotifyProgress(1);
                    this.doNotifyStateChange(Preloader.StateChangeNotification.Type.BEFORE_LOAD);
                    PlatformImpl.runAndWait(() -> {
                        ParametersImpl.registerParameters(applicationImpl, new ParametersImpl(args));
                        PlatformImpl.setApplicationName(applicationImpl.getClass());
                    });
                }
                if (!this.errored && currAppState.get() != LaunchState.EXIT) {
                    this.doNotifyStateChange(Preloader.StateChangeNotification.Type.BEFORE_INIT);
                    try {
                        applicationImpl.init();
                    } catch (Exception ex) {
                        this.exceptions.put("Exception while handling application init event", Pair.of(ex, true));
                        this.errored = true;
                    }
                }
                IControllerFactoryProvider provider = SimpliFX.appDIEnv == null ? new FXMLControllerFactoryProvider() : new DIControllerFactoryProvider(SimpliFX.appDIEnv);
                if (SimpliFX.experimentalEnabled) {
                    provider = new SubclassControllerFactoryProvider(new SubclassingClassLoader(applicationListener.getClass().getClassLoader()), provider);
                }
                final IControllerGroup mainGroup = new ControllerGroupImpl("main", applicationListener.getClass().getAnnotation(ApplicationEntryPoint.class).value(),
                        provider, SimpliFX.globalI18N, SimpliFX.globalResources, SimpliFX.globalPropertyRegistry,
                        SimpliFX.defaultNotificationHandler, null, null);
                if (!this.errored && currAppState.get() != LaunchState.EXIT) {
                    this.doNotifyStateChange(Preloader.StateChangeNotification.Type.BEFORE_START);
                    PlatformImpl.runAndWait(() -> {
                        try {
                            mainGroup.getOrConstructController(mainGroup.getStartControllerClass());
                            this.currAppState.set(LaunchState.START);
                            final Stage primary = new Stage();
                            primary.setOnCloseRequest(w -> PlatformImpl.exit());
                            mainGroup.start(primary);
                            final StageConfig config = Launcher.createStage(primary, applicationListener.getClass()).getLeft();
                            StageHelper.setPrimary(primary, true);
                            this.applicationImpl.start(primary);
                            if (config.autoShow() && !Launcher.wasVisible(primary)) {
                                primary.show();
                            }
                        } catch (Exception ex) {
                            this.exceptions.put("Exception while starting application", Pair.of(ex, true));
                            this.errored = true;
                        }
                    });
                }
                if (!errored) {
                    this.applicationExitSem.acquire();
                }
                if (this.currAppState.get().id >= LaunchState.EXIT.id) {
                    PlatformImpl.runAndWait(() -> {
                        try {
                            mainGroup.destroy();
                            applicationImpl.stop();
                        } catch (Exception ex) {
                            this.exceptions.put("Exception while handling application stop event", Pair.of(ex, true));
                            this.errored = true;
                        }
                    });
                }
                if (errored) {
                    final Map.Entry<String, Pair<Throwable, Boolean>> ex = this.exceptions.entrySet().iterator().next();
                    if (ex.getValue().getRight() && this.doNotifyError(ex.getKey(), ex.getValue().getLeft())) {
                        return;
                    }
                    throw new RuntimeException(ex.getKey(), ex.getValue().getLeft());
                }
            } finally {
                PlatformImpl.removeListener(exitListener);
                PlatformImpl.tkExit();
            }
        }

        private enum LaunchState {
            INIT(0),
            START(1),
            EXIT(2);

            private final int id;

            LaunchState(int id) {
                this.id = id;
            }

        }

        private final class ExitListener implements PlatformImpl.FinishListener {

            @Override
            public void idle(boolean implicitExit) {
                if (implicitExit) {
                    if (Launcher.this.currAppState.get().id >= LaunchState.START.id) {
                        Launcher.this.applicationExitSem.release();
                    } else if (Launcher.this.currPreState.get().id >= LaunchState.START.id) {
                        Launcher.this.preloaderExitSem.release();
                    }
                }
            }

            @Override
            public void exitCalled() {
                Launcher.this.currAppState.set(LaunchState.EXIT);
                Launcher.this.applicationExitSem.release();
            }

        }

    }

}
