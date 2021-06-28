package de.intelligence.bachelorarbeit.simplifx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.annotation.ApplicationEntryPoint;
import de.intelligence.bachelorarbeit.simplifx.classpath.ClassDiscovery;
import de.intelligence.bachelorarbeit.simplifx.classpath.DiscoveryContextBuilder;
import de.intelligence.bachelorarbeit.simplifx.classpath.IDiscoveryResult;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

public final class SimpliFX {

    private static IDiscoveryResult discoveryResult;
    private static ClasspathScanPolicy scanPolicy = ClasspathScanPolicy.GLOBAL;
    private static Class<?> callerClass;

    public static void setClasspathScanPolicy(ClasspathScanPolicy scanPolicy) {
        SimpliFX.scanPolicy = scanPolicy;
    }

    public static void launch() {
        final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        callerClass = walker.getCallerClass();

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
                launchImpl(classOpt.get());
                return;
            }
        }
        Conditions.checkNull(applicationClass, "Could not find application class!");
    }

    public static void launch(Class<?> applicationClass) {
        Conditions.checkNull(applicationClass, "Class cannot be null!");
        Conditions.checkCondition(SimpliFX.validateClassType(applicationClass) && Reflection.reflect(applicationClass)
                .isAnnotationPresent(ApplicationEntryPoint.class), "Invalid application class specified!");
        launchImpl(applicationClass);
    }

    private static void launchImpl(Class<?> applicationClass) {
        // ready to launch
        System.out.println(new String(SimpliFXConstants.BANNER));
        System.out.println("READY: " + applicationClass);
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

}
