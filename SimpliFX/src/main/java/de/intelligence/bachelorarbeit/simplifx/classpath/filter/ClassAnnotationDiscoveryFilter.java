package de.intelligence.bachelorarbeit.simplifx.classpath.filter;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import de.intelligence.bachelorarbeit.simplifx.classpath.ClasspathDiscoveryException;

public class ClassAnnotationDiscoveryFilter implements IDiscoveryFilter<ClassReader> {

    private final Class<? extends Annotation> annotation;

    public ClassAnnotationDiscoveryFilter(Class<? extends Annotation> annotation) {
        final Optional<Target> targetOpt = Optional.ofNullable(annotation.getAnnotation(Target.class));
        if (targetOpt.isPresent() && !Arrays.asList(targetOpt.get().value()).contains(ElementType.TYPE)) {
            throw new ClasspathDiscoveryException("Specified annotation cannot be applied to class types.");
        }
        final Optional<Retention> retentionOpt = Optional.ofNullable(annotation.getAnnotation(Retention.class));
        if (retentionOpt.isPresent() && retentionOpt.get().value() != RetentionPolicy.RUNTIME) {
            throw new ClasspathDiscoveryException("Specified annotation is not visible at runtime.");
        }
        this.annotation = annotation;
    }

    @Override
    public boolean matches(ClassReader reader) {
        final AtomicBoolean hasAnnotation = new AtomicBoolean(false);
        reader.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (ClassAnnotationDiscoveryFilter.this.annotation.getName()
                        .equals(Type.getType(descriptor).getClassName())) {
                    hasAnnotation.set(true);
                }
                return super.visitAnnotation(descriptor, visible);
            }
        }, 0);
        return hasAnnotation.get();
    }

}
