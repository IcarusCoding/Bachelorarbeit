package de.intelligence.bachelorarbeit.reflectionutils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The {@link FieldReflection} class provides methods to perform reflective operations on {@link Field} level.
 *
 * @author Deniz Groenhoff
 */
public final class FieldReflection extends ReflectableMember<Field> {

    FieldReflection(Field field) {
        super(field);
    }

    FieldReflection(Field field, Object accessor) {
        super(field, accessor);
    }

    /**
     * Sets the {@link Field} to the given value
     *
     * @param value The value
     */
    public void set(Object value) {
        Reflection.makeAccessibleAndExecute(super.reflectable,
                Modifier.isStatic(super.reflectable.getModifiers()) ? null : super.accessor, super.shouldForceAccess,
                () -> super.reflectable.set(accessor, value));
    }

    /**
     * Retrieves the type-inferred value of the {@link Field}
     *
     * @return The type inferred value
     */
    public <T> T getUnsafe() {
        return Reflection.unsafeCast(get());
    }

    /**
     * Retrieves the value of the {@link Field}
     *
     * @return The value
     */
    public Object get() {
        return Reflection.makeAccessibleAndExecute(super.reflectable,
                Modifier.isStatic(super.reflectable.getModifiers()) ? null : super.accessor, super.shouldForceAccess,
                () -> super.reflectable.get(super.accessor));
    }

    /**
     * Takes the instance of the {@link Field} and creates a new {@link InstanceReflection} from that
     *
     * @return A new {@link InstanceReflection} which contains the instance of the value from the {@link Field}
     */
    public InstanceReflection toInstanceReflection() {
        return Reflection.reflect(get());
    }

    @Override
    public FieldReflection forceAccess() {
        super.shouldForceAccess = true;
        return this;
    }

}
