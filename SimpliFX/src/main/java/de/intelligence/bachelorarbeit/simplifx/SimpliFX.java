package de.intelligence.bachelorarbeit.simplifx;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
import de.intelligence.bachelorarbeit.simplifx.di.DIAnnotation;
import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;
import de.intelligence.bachelorarbeit.simplifx.di.IDIEnvironmentFactory;
import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.exception.ApplicationConstructionException;
import de.intelligence.bachelorarbeit.simplifx.exception.SimpliFXException;
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

//TODO remove some static behaviour
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
    private static boolean launched;

    public static void setClasspathScanPolicy(ClasspathScanPolicy scanPolicy) {
        SimpliFX.scanPolicy = scanPolicy;
    }

    public static void setDefaultNotificationHandler(Function<Pane, INotificationDialog> defaultNotificationHandler) {
        SimpliFX.defaultNotificationHandler = defaultNotificationHandler;
    }

    public static void launch(String... args) {
        final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        SimpliFX.callerClass = walker.getCallerClass();
        SimpliFX.startDiscovery();
        SimpliFX.launch(SimpliFX.findApplicationClass(), args);
    }

    public static void launchWithPreloader(String... args) {
        final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        SimpliFX.callerClass = walker.getCallerClass();
        SimpliFX.startDiscovery();
        SimpliFX.launch(SimpliFX.findApplicationClass(), SimpliFX.findPreloaderClass(), args);
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

    public static void launch(Class<?> appEntrypointClass, String... args) {
        SimpliFX.checkLaunch();
        SimpliFX.launchImpl(SimpliFX.validateEntrypointClass(appEntrypointClass, ApplicationEntryPoint.class)
                .forceAccess().instantiate().getReflectable(), null, args);
    }

    public static void launch(Class<?> applicationClass, Class<?> preloaderClass, String... args) {
        Conditions.checkCondition(applicationClass != preloaderClass,
                "Different classes needed for application and preloader entrypoint!");
        SimpliFX.checkLaunch();
        SimpliFX.launchImpl(SimpliFX.validateEntrypointClass(applicationClass, ApplicationEntryPoint.class)
                        .forceAccess().instantiate().getReflectable(),
                SimpliFX.validateEntrypointClass(preloaderClass, PreloaderEntryPoint.class)
                        .forceAccess().instantiate().getReflectable(), args);
    }

    public static void launch(Object applicationListener, String... args) {
        SimpliFX.validateEntrypointInstance(applicationListener, ApplicationEntryPoint.class);
        SimpliFX.checkLaunch();
        SimpliFX.launchImpl(applicationListener, null, args);
    }

    public static void launch(Object applicationListener, Object preloaderListener, String... args) {
        Conditions.checkCondition(applicationListener.getClass() != preloaderListener.getClass(),
                "Different classes needed for application and preloader entrypoint!");
        SimpliFX.validateEntrypointInstance(applicationListener, ApplicationEntryPoint.class);
        SimpliFX.validateEntrypointInstance(preloaderListener, PreloaderEntryPoint.class);
        SimpliFX.checkLaunch();
        SimpliFX.launchImpl(applicationListener, preloaderListener, args);
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
        SimpliFX.globalPropertyRegistry = new PropertyRegistry(applicationListener.getClass().getClassLoader());
        final Class<?> applicationClass = applicationListener.getClass();

        System.out.println(new String(SimpliFXConstants.BANNER));
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
        configDetector.getAnnotations().stream().flatMap(a -> Arrays.stream(a.value())).distinct()
                .forEach(path -> SimpliFX.globalPropertyRegistry.loadFrom(path));
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
            throw new RuntimeException("Unexpected exception thrown: ", ex);
        }
        if (launcherException.get() != null) {
            throw launcherException.get();
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
                        return; // filter default
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
                        .reflectMethod("create", Object.class,
                                (Class<?>) ((ParameterizedType) factory.getGenericInterfaces()[0])
                                        .getActualTypeArguments()[0]);
                SimpliFX.appDIEnv = methodRef.invokeUnsafe(applicationListener, annotation);
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
        private final Map<String, Throwable> exceptions;

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
                this.exceptions.put("Exception on FX thread", th);
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
                        this.exceptions.put("Exception while handling preloader init event", ex);
                        this.errored = true;
                    }
                    if (!this.errored && !currAppState.get().equals(LaunchState.EXIT)) {
                        PlatformImpl.runAndWait(() -> {
                            this.currPreState.set(LaunchState.START);
                            final Pair<StageConfig, Stage> stageConfig = this.createStage(new Stage(), preloaderListener.getClass());
                            final Stage primary = stageConfig.getRight();
                            StageHelper.setPrimary(primary, true);
                            try {
                                this.preloaderImpl.start(primary);
                                if (stageConfig.getLeft().autoShow() && !this.wasVisible(primary)) {
                                    primary.show();
                                }
                            } catch (Exception ex) {
                                this.exceptions.put("Exception while handling preloader start event", ex);
                                this.errored = true;
                            }
                        });
                    }
                    if (!this.errored && !currAppState.get().equals(LaunchState.EXIT)) {
                        this.doNotifyProgress(0);
                    }
                }
                if (!errored && !currAppState.get().equals(LaunchState.EXIT)) {
                    this.doNotifyProgress(1);
                    this.doNotifyStateChange(Preloader.StateChangeNotification.Type.BEFORE_LOAD);
                    PlatformImpl.runAndWait(() -> {
                        ParametersImpl.registerParameters(applicationImpl, new ParametersImpl(args));
                        PlatformImpl.setApplicationName(applicationImpl.getClass());
                    });
                }
                if (!this.errored && !currAppState.get().equals(LaunchState.EXIT)) {
                    this.doNotifyStateChange(Preloader.StateChangeNotification.Type.BEFORE_INIT);
                    try {
                        applicationImpl.init();
                    } catch (Exception ex) {
                        this.exceptions.put("Exception while handling application init event", ex);
                        this.errored = true;
                    }
                }
                final IControllerGroup mainGroup = new ControllerGroupImpl("main", applicationListener.getClass().getAnnotation(ApplicationEntryPoint.class).value(),
                        SimpliFX.appDIEnv == null ? new FXMLControllerFactoryProvider() : new DIControllerFactoryProvider(SimpliFX.appDIEnv),
                        SimpliFX.globalI18N, SimpliFX.globalResources, SimpliFX.globalPropertyRegistry,
                        SimpliFX.defaultNotificationHandler, null, null);
                if (!this.errored && !currAppState.get().equals(LaunchState.EXIT)) {
                    this.doNotifyStateChange(Preloader.StateChangeNotification.Type.BEFORE_START);
                    PlatformImpl.runAndWait(() -> {
                        try {
                            mainGroup.getOrConstructController(mainGroup.getStartControllerClass());
                            this.currAppState.set(LaunchState.START);
                            final Stage primary = new Stage();
                            primary.setOnCloseRequest(w -> PlatformImpl.exit());
                            mainGroup.start(primary);
                            final StageConfig config = this.createStage(primary, applicationListener.getClass()).getLeft();
                            StageHelper.setPrimary(primary, true);
                            this.applicationImpl.start(primary);
                            if (config.autoShow() && !this.wasVisible(primary)) {
                                primary.show();
                            }
                        } catch (Exception ex) {
                            this.exceptions.put("Exception while starting application", ex);
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
                            this.exceptions.put("Exception while handling application stop event", ex);
                            this.errored = true;
                        }
                    });
                }
                if (errored) {
                    final Map.Entry<String, Throwable> ex = this.exceptions.entrySet().iterator().next();
                    throw new RuntimeException(ex.getKey(), ex.getValue());
                }
            } finally {
                PlatformImpl.removeListener(exitListener);
                PlatformImpl.tkExit();
            }
        }

        private boolean wasVisible(Stage stage) {
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

        private void doNotifyError(String message, Throwable cause) {
            if (this.preloaderImpl == null) {
                return;
            }
            PlatformImpl.runAndWait(() -> this.preloaderImpl
                    .handleErrorNotification(new Preloader.ErrorNotification(null, message, cause)));
        }

        private void notifyPreloader(Preloader.PreloaderNotification notification) {
            if (this.preloaderImpl == null) {
                return;
            }
            this.preloaderImpl.notifyPreloader(notification);
        }

        private Pair<StageConfig, Stage> createStage(Stage stage, Class<?> entrypointClass) {
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
