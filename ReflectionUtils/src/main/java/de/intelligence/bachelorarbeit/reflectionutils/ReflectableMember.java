package de.intelligence.bachelorarbeit.reflectionutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

/**
 * The {@link ReflectableMember} class is an abstract class for reflection scopes that perform reflection on {@link Member} level
 *
 * @author Deniz Groenhoff
 */
abstract class ReflectableMember<T extends Member & AnnotatedElement> extends ReflectableScope<T> implements Annotatable {

    protected boolean shouldForceAccess;
    protected Object accessor;

    protected ReflectableMember(T reflectable) {
        super(reflectable);
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

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return super.reflectable.isAnnotationPresent(annotation);
    }

    @Override
    public <S extends Annotation> S getAnnotation(Class<S> annotation) {
        return super.reflectable.getDeclaredAnnotation(annotation);
    }

    @Override
    public AnnotatedElement getAnnotatableElement() {
        return super.reflectable;
    }

}
