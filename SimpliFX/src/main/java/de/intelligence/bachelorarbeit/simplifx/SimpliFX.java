package de.intelligence.bachelorarbeit.simplifx;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.sun.javafx.application.ParametersImpl;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.stage.StageHelper;
import com.sun.javafx.util.Logging;

import de.intelligence.bachelorarbeit.reflectionutils.ClassReflection;
import de.intelligence.bachelorarbeit.reflectionutils.ConstructorReflection;
import de.intelligence.bachelorarbeit.reflectionutils.MethodReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.annotation.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.annotation.DIAnnotation;
import de.intelligence.bachelorarbeit.simplifx.annotation.PostConstruct;
import de.intelligence.bachelorarbeit.simplifx.annotation.PreloaderEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.annotation.ResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.annotation.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.application.DIConfig;
import de.intelligence.bachelorarbeit.simplifx.classpath.ClassDiscovery;
import de.intelligence.bachelorarbeit.simplifx.classpath.DiscoveryContextBuilder;
import de.intelligence.bachelorarbeit.simplifx.classpath.IDiscoveryResult;
import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;
import de.intelligence.bachelorarbeit.simplifx.di.IDIEnvironmentFactory;
import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.injection.AnnotatedFieldDetector;
import de.intelligence.bachelorarbeit.simplifx.injection.IAnnotatedFieldDetector;
import de.intelligence.bachelorarbeit.simplifx.localization.CompoundResourceBundle;
import de.intelligence.bachelorarbeit.simplifx.localization.I18N;
import de.intelligence.bachelorarbeit.simplifx.localization.II18N;
import de.intelligence.bachelorarbeit.simplifx.logging.SimpliFXLogger;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

public final class SimpliFX {

    private static final SimpliFXLogger LOG = SimpliFXLogger.create(SimpliFX.class);

    private static IDiscoveryResult discoveryResult;
    private static ClasspathScanPolicy scanPolicy = ClasspathScanPolicy.LOCAL;
    private static Class<?> callerClass;
    private static Injector globalInjector;
    private static II18N globalI18N;
    private static DIEnvironment appDIEnv;

    public static void setClasspathScanPolicy(ClasspathScanPolicy scanPolicy) {
        SimpliFX.scanPolicy = scanPolicy;
    }

    public static void launch() {
        final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        SimpliFX.callerClass = walker.getCallerClass();
        SimpliFX.startDiscovery();
        SimpliFX.launch(SimpliFX.findApplicationClass());
    }

    public static void launchWithPreloader() {
        final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        SimpliFX.callerClass = walker.getCallerClass();
        SimpliFX.startDiscovery();
        SimpliFX.launch(SimpliFX.findApplicationClass(), SimpliFX.findPreloaderClass());
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

    public static void launch(Class<?> appEntrypointClass) {
        SimpliFX.launchImpl(SimpliFX.validateEntrypointClass(appEntrypointClass, ApplicationEntryPoint.class)
                .forceAccess().instantiate().getReflectable(), null);
    }

    public static void launch(Class<?> applicationClass, Class<?> preloaderClass) {
        Conditions.checkCondition(applicationClass != preloaderClass,
                "Different classes needed for application and preloader entrypoint!");
        SimpliFX.launchImpl(SimpliFX.validateEntrypointClass(applicationClass, ApplicationEntryPoint.class)
                        .forceAccess().instantiate().getReflectable(),
                SimpliFX.validateEntrypointClass(preloaderClass, PreloaderEntryPoint.class)
                        .forceAccess().instantiate().getReflectable());
    }


    public static void launch(Object applicationListener) {
        SimpliFX.validateEntrypointInstance(applicationListener, ApplicationEntryPoint.class);
        SimpliFX.launchImpl(applicationListener, null);
    }

    public static void launch(Object applicationListener, Object preloaderListener) {
        Conditions.checkCondition(applicationListener.getClass() != preloaderListener.getClass(),
                "Different classes needed for application and preloader entrypoint!");
        SimpliFX.validateEntrypointInstance(applicationListener, ApplicationEntryPoint.class);
        SimpliFX.validateEntrypointInstance(preloaderListener, PreloaderEntryPoint.class);
        SimpliFX.launchImpl(applicationListener, preloaderListener);
    }

    private static void launchImpl(Object applicationListener, Object preloaderListener) {
        Logging.getJavaFXLogger().disableLogging();
        SimpliFX.globalInjector = Guice.createInjector(new DIConfig());
        final Class<?> applicationClass = applicationListener.getClass();

        System.out.println(new String(SimpliFXConstants.BANNER));
        SimpliFX.LOG.info("Starting application with entrypoint: " + applicationClass.getName());

        final Application appImpl = SimpliFX.globalInjector.getInstance(Application.class);
        final Preloader preImpl = SimpliFX.globalInjector.getInstance(Preloader.class);

        final AnnotatedFieldDetector<ResourceBundle> bundleDetector = new AnnotatedFieldDetector<>(ResourceBundle.class,
                applicationListener, preloaderListener);
        SimpliFX.globalI18N = SimpliFX.setupI18N(bundleDetector);
        bundleDetector.injectValue(SimpliFX.globalI18N, true, ex -> {
            System.out.println("EXCEPTION");
            //TODO handle
        });

        try {
            SimpliFX.setupDI(applicationListener);
        } catch (Exception ex) {
            LOG.error("There was an error while trying to setup the DI framework:");
            ex.printStackTrace();
            return;
        }

        // TODO init all subsystems -> create main controller & controller system, ...
        //TODO beautify at the end
        Reflection.reflect(applicationListener).iterateMethods(methodRef ->
                methodRef.isAnnotationPresent(PostConstruct.class), methodRef -> methodRef.forceAccess().invoke());

        final Thread fxLauncherThread = new Thread(() -> {
            final Launcher l = new Launcher(appImpl, preImpl);
            try {
                l.launchApplication(applicationListener, preloaderListener, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "SimpliFX Launcher Thread");
        fxLauncherThread.start();
        try {
            fxLauncherThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static II18N setupI18N(IAnnotatedFieldDetector<ResourceBundle> bundleDetector) {
        bundleDetector.findAllFields((f, a) -> f.canAccept(I18N.class));
        final Map<Locale, List<java.util.ResourceBundle>> bundleMap = new HashMap<>();
        bundleDetector.getAnnotations()
                /*.forEach(bundle -> I18N.findAllBundlesWithBaseName(bundle.directory(), bundle.name(), ex -> {
                    ex.printStackTrace();
                    //TODO handle
                }).forEach((l, b) -> {
                    if (!bundleMap.containsKey(l)) {
                        bundleMap.put(l, new ArrayList<>());
                    }
                    bundleMap.get(l).add(b);
                }));*/
                .stream().filter(b -> !b.value().isBlank()).forEach(b -> Arrays.stream(Locale.getAvailableLocales())
                .map(locale -> java.util.ResourceBundle.getBundle(b.value(), locale))
                .filter(Conditions.distinct(java.util.ResourceBundle::getLocale)).forEach(bundle -> {
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
                final DIAnnotation diAnnotation = annotation.annotationType().getAnnotation(DIAnnotation.class);
                final Class<? extends IDIEnvironmentFactory<? extends Annotation>> factory = diAnnotation.value();
                final ClassReflection classRef = Reflection.reflect(factory);
                final Optional<ConstructorReflection> constructorRefOpt = classRef.hasConstructor();
                if (constructorRefOpt.isEmpty()) {
                    LOG.warn("Failed to instantiate DI factory " + classRef.getReflectable().getSimpleName()
                            + ". Reason: Missing default constructor.");
                    break;
                }
                final Class<?> clazz = (Class<?>) ((ParameterizedType) factory.getGenericInterfaces()[0])
                        .getActualTypeArguments()[0];
                final IDIEnvironmentFactory<?> factoryInstance = constructorRefOpt.get().instantiateUnsafeAndGet();
                final MethodReflection methodRef = Reflection.reflect(factoryInstance)
                        .reflectMethod("create", Object.class, clazz);
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

        private <T extends Application, S extends Preloader> Launcher(T applicationImpl, S preloaderImpl) {
            this.applicationImpl = applicationImpl;
            this.preloaderImpl = preloaderImpl;
            this.applicationExitSem = new Semaphore(0);
            this.preloaderExitSem = new Semaphore(0);
            this.currAppState = new AtomicReference<>(LaunchState.INIT);
            this.currPreState = new AtomicReference<>(LaunchState.INIT);
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
            final ExitListener exitListener = new ExitListener();
            PlatformImpl.addListener(exitListener);
            if (preloaderListener != null) {
                PlatformImpl.runAndWait(
                        () -> ParametersImpl.registerParameters(this.preloaderImpl, new ParametersImpl(args)));
                this.preloaderImpl.init();
                PlatformImpl.runAndWait(() -> {
                    this.currPreState.set(LaunchState.START);
                    final Stage primary = this.createStage(preloaderListener.getClass());
                    StageHelper.setPrimary(primary, true);
                    try {
                        this.preloaderImpl.start(primary);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                this.doNotifyProgress(0);
            }
            this.doNotifyProgress(1);
            this.doNotifyStateChange(Preloader.StateChangeNotification.Type.BEFORE_LOAD);
            PlatformImpl.runAndWait(() -> {
                ParametersImpl.registerParameters(applicationImpl, new ParametersImpl(args));
                PlatformImpl.setApplicationName(applicationImpl.getClass());
            });
            this.doNotifyStateChange(Preloader.StateChangeNotification.Type.BEFORE_INIT);
            applicationImpl.init();
            this.doNotifyStateChange(Preloader.StateChangeNotification.Type.BEFORE_START);
            PlatformImpl.runAndWait(() -> {
                this.currAppState.set(LaunchState.START);
                final Stage primary = this.createStage(applicationListener.getClass());
                StageHelper.setPrimary(primary, true);
                try {
                    applicationImpl.start(primary);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            this.applicationExitSem.acquire();
            if (this.currAppState.get().id >= LaunchState.EXIT.id) {
                PlatformImpl.runAndWait(() -> {
                    try {
                        applicationImpl.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            PlatformImpl.removeListener(exitListener);
            PlatformImpl.tkExit();
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

        private Stage createStage(Class<?> entrypointClass) {
            final Stage stage = new Stage();
            Reflection.reflect(entrypointClass).getAnnotation(StageConfig.class).ifPresent(config -> {
                stage.setTitle(config.title());
                stage.initStyle(config.style());
                stage.setAlwaysOnTop(config.alwaysTop());
                boolean iconError = false;
                if (!config.iconPath().isBlank()) {
                    try (InputStream stream = entrypointClass.getResourceAsStream(config.iconPath())) {
                        if (stream != null) {
                            stage.getIcons().add(new Image(stream));
                        } else {
                            iconError = true;
                        }
                    } catch (IOException ignored) {
                        iconError = true;
                    }
                }
                if (iconError) {
                    LOG.error("Could not load icon from " + config.iconPath() + ".");
                }
                stage.setResizable(config.resizeable());
                stage.setOnCloseRequest(w -> PlatformImpl.exit());
                if (config.autoShow()) {
                    stage.show();
                }
            });
            return stage;
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
