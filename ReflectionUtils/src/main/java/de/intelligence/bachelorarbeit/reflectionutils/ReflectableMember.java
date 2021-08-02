package de.intelligence.bachelorarbeit.reflectionutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Optional;

/**
 * The {@link ReflectableMember} class is an abstract class for reflection scopes that perform reflection on {@link Member} level
 *
 * @author Deniz Groenhoff
 */
abstract class ReflectableMember<T extends Member & AnnotatedElement> extends ReflectableScope<T> implements Annotatable, ExceptionHandleable {

    protected boolean shouldForceAccess;
    protected Object accessor;
    protected IReflectionExceptionHandler handler;

    protected ReflectableMember(T reflectable) {
        this(reflectable, null);
    }

    protected ReflectableMember(T reflectable, Object accessor) {
        super(reflectable);
        this.accessor = accessor;
    }

    /**
     * Forces access on a member
     *
     * @return This instance
     */
    public abstract <E> E forceAccess();

    /**
     * Sets the accessor for accessing the encapsulated {@link Member}
     *
     * @param accessor The accessor
     */
    public void setAccessor(Object accessor) {
        this.accessor = accessor;
    }

    /**
     * Retrieves the current accessor
     *
     * @return The current accessor
     */
    public Object getAccessor() {
        return this.accessor;
    }

    /**
     * Retrieves the current accessor
     *
     * @return The type-inferred accessor
     */
    public <T> T getAccessorUnsafe() {
        return (T) this.accessor;
    }

    @Override
    public void setExceptionHandler(IReflectionExceptionHandler handler) {
        if (handler != null) {
            this.handler = handler;
        }
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return super.reflectable.getAnnotationsByType(annotation).length != 0;
    }

    @Override
    public <S extends Annotation> Optional<S> getAnnotation(Class<S> annotation) {
        return Optional.ofNullable(super.reflectable.getDeclaredAnnotation(annotation));
    }

    @Override
    public AnnotatedElement getAnnotatableElement() {
        return super.reflectable;
    }

}
