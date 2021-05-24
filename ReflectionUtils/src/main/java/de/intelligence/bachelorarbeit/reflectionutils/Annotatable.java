package de.intelligence.bachelorarbeit.reflectionutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * A {@link ReflectableScope} should be Annotatable if the underlying reflectable member is an {@link AnnotatedElement}
 *
 * @author Deniz Groenhoff
 */
public interface Annotatable {

    /**
     * Checks if the {@link AnnotatedElement} is annotated by the specified annotation
     *
     * @param annotation The {@link Annotation}
     * @return If the {@link AnnotatedElement} is annotated by the specified annotation
     */
    boolean isAnnotationPresent(Class<? extends Annotation> annotation);

    /**
     * Retrieves the specified annotation from the {@link AnnotatedElement}
     *
     * @param annotation The {@link Annotation}
     * @return The annotation if present, null otherwise
     */
    <S extends Annotation> S getAnnotation(Class<S> annotation);

    /**
     * Retrieves the {@link AnnotatedElement}
     *
     * @return The {@link AnnotatedElement}
     */
    AnnotatedElement getAnnotatableElement();

}
