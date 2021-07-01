package de.intelligence.bachelorarbeit.simplifx;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.sun.javafx.application.ParametersImpl;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.stage.StageHelper;
import com.sun.javafx.util.Logging;

import de.intelligence.bachelorarbeit.reflectionutils.ConstructorReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.annotation.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.annotation.StageConfig;
import de.intelligence.bachelorarbeit.simplifx.application.DIConfig;
import de.intelligence.bachelorarbeit.simplifx.classpath.ClassDiscovery;
import de.intelligence.bachelorarbeit.simplifx.classpath.DiscoveryContextBuilder;
import de.intelligence.bachelorarbeit.simplifx.classpath.IDiscoveryResult;
import de.intelligence.bachelorarbeit.simplifx.event.IEventEmitter;
import de.intelligence.bachelorarbeit.simplifx.internaldi.Injector;
import de.intelligence.bachelorarbeit.simplifx.logging.SimpliFXLogger;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

public final class SimpliFX {

    private static final SimpliFXLogger LOG = SimpliFXLogger.create(SimpliFX.class);

    private static IDiscoveryResult discoveryResult;
    private static ClasspathScanPolicy scanPolicy = ClasspathScanPolicy.GLOBAL;
    private static Class<?> callerClass;
    private static Injector globalInjector;

    public static void setClasspathScanPolicy(ClasspathScanPolicy scanPolicy) {
        SimpliFX.scanPolicy = scanPolicy;
    }

    public static void launch() {
        final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        SimpliFX.callerClass = walker.getCallerClass();

        Class<?> applicationClass = null;
        if (Reflection.reflect(callerClass).isAnnotationPresent(ApplicationEntryPoint.class)
                && SimpliFX.validateClassType(callerClass)) {
            applicationClass = callerClass;
        } else {
            final List<Class<?>> declared = SimpliFX
                    .findDeclaredClassesAnnotatedBy(callerClass, ApplicationEntryPoint.class);
            if (!declared.isEmpty()) {
                applicationClass = declared.get(0);
            }
        }
        if (applicationClass == null) {
            SimpliFX.startDiscovery();
            final Optional<Class<?>> classOpt = SimpliFX.discoveryResult
                    .findClassesAnnotatedBy(ApplicationEntryPoint.class).stream()
                    .filter(SimpliFX::validateClassType).findFirst();
            if (classOpt.isPresent()) {
                SimpliFX.launch(classOpt.get());
                return;
            }
        }
        Conditions.checkNull(applicationClass, "Could not find application class!");
    }

    public static void launch(Class<?> applicationClass) {
        Conditions.checkNull(applicationClass, "Class cannot be null!");
        Conditions.checkCondition(SimpliFX.validateEntryPoint(applicationClass),
                "Invalid application class specified!");
        final ConstructorReflection conRef = Conditions
                .nullOnException(() -> Reflection.reflect(applicationClass).findConstructor());
        Conditions.checkNull(conRef, "Unable to locate default constructor!");
        SimpliFX.launchImpl(conRef.forceAccess().instantiate().getReflectable());
    }

    public static void launch(Object obj) {
        Conditions.checkNull(obj, "Instance cannot be null!");
        Conditions.checkCondition(SimpliFX.validateEntryPoint(obj.getClass()),
                "Invalid application instance specified!");
        SimpliFX.launchImpl(obj);
    }

    private static void launchImpl(Object applicationListener) {
        Logging.getJavaFXLogger().disableLogging();
        SimpliFX.globalInjector = new Injector(new DIConfig());
        final Class<?> applicationClass = applicationListener.getClass();
        // ready to launch
        System.out.println(new String(SimpliFXConstants.BANNER));
        System.out.println("READY: " + applicationClass);

        final Application appImpl = SimpliFX.globalInjector.get(Application.class);
        SimpliFX.globalInjector.get(IEventEmitter.class).register(applicationListener);
        final Thread fxLauncherThread = new Thread(() -> {
            Launcher l = new Launcher(applicationClass);
            try {
                l.launchApplication(appImpl, null, null);
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
        // TODO find preloader
        // TODO init all subsystems -> create main controller & controller system, custom fxml loader, ...
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
            if (Reflection.reflect(declaredClass).isAnnotationPresent(annotation)
                    && SimpliFX.validateClassType(declaredClass)) {
                validClasses.add(declaredClass);
            }
        }
        return validClasses;
    }

    private static boolean validateClassType(Class<?> clazz) {
        return (Modifier.isStatic(clazz.getModifiers()) || clazz.getEnclosingClass() == null)
                && !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isEnum();
    }

    private static boolean validateEntryPoint(Class<?> clazz) {
        return SimpliFX.validateClassType(clazz) && Reflection.reflect(clazz)
                .isAnnotationPresent(ApplicationEntryPoint.class);
    }

    private static final class Launcher {

        private static final AtomicBoolean toolkitInitialized = new AtomicBoolean(false);

        private final Class<?> applicationClass;
        private final Semaphore applicationExitSem;
        private final AtomicReference<LaunchState> currAppState;

        private Launcher(Class<?> applicationClass) {
            this.applicationClass = applicationClass;
            this.applicationExitSem = new Semaphore(0);
            this.currAppState = new AtomicReference<>(LaunchState.INIT);
        }

        private <T extends Application, S extends Preloader>
        void launchApplication(T applicationImpl, S preloaderImpl, String[] args) throws Exception {
            if (!Launcher.toolkitInitialized.getAndSet(true)) {
                final Semaphore sem = new Semaphore(0);
                PlatformImpl.startup(sem::release);
                sem.acquire();
            }
            final ExitListener exitListener = new ExitListener();
            PlatformImpl.addListener(exitListener);
            PlatformImpl.runAndWait(() -> {
                ParametersImpl.registerParameters(applicationImpl, new ParametersImpl(args));
                PlatformImpl.setApplicationName(applicationImpl.getClass());
            });
            applicationImpl.init();
            PlatformImpl.runAndWait(() -> {
                this.currAppState.set(LaunchState.START);
                final Stage primary = this.createStage();
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

        private Stage createStage() {
            final Stage stage = new Stage();
            Reflection.reflect(applicationClass).getAnnotation(StageConfig.class).ifPresent(config -> {
                stage.setTitle(config.title());
                stage.initStyle(config.style());
                stage.setAlwaysOnTop(config.alwaysTop());
                boolean iconError = false;
                if (!config.iconPath().isBlank()) {
                    try (InputStream stream = applicationClass.getResourceAsStream(config.iconPath())) {
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
                    LOG.warn("Could not load application icon from {}.");
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
