package de.intelligence.bachelorarbeit.simplifx.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class FieldReflection extends ReflectableMember<Field> {

    FieldReflection(Field field) {
        super(field);
    }

    FieldReflection(Field field, Object accessor) {
        super(field, accessor);
    }

    public void set(Object value) {
        Reflection.makeAccessibleAndExecute(super.reflectable,
                Modifier.isStatic(super.reflectable.getModifiers()) ? null : super.accessor, super.shouldForceAccess,
                () -> super.reflectable.set(accessor, value));
    }

    public <T> T getUnsafe() {
        return Reflection.unsafeCast(get());
    }

    public Object get() {
        return Reflection.makeAccessibleAndExecute(super.reflectable,
                Modifier.isStatic(super.reflectable.getModifiers()) ? null : super.accessor, super.shouldForceAccess,
                () -> super.reflectable.get(super.accessor));
    }

    public InstanceReflection toInstanceReflection() {
        return Reflection.reflect(get());
    }

    @Override
    public FieldReflection forceAccess() {
        super.shouldForceAccess = true;
        return this;
    }

}
