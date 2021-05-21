package de.intelligence.bachelorarbeit.simplifx.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class MethodReflection extends ReflectableMember<Method> {

    MethodReflection(Method method) {
        super(method);
    }

    MethodReflection(Method method, Object accessor) {
        super(method, accessor);
    }

    public <E> E invokeUnsafe(Object... args) {
        return Reflection.unsafeCast(invoke(args));
    }

    public Object invoke(Object... args) {
        return Reflection.makeAccessibleAndExecute(super.reflectable,
                Modifier.isStatic(super.reflectable.getModifiers()) ? null : super.accessor, super.shouldForceAccess,
                () -> super.reflectable.invoke(super.accessor, args));
    }

    @Override
    public MethodReflection forceAccess() {
        super.shouldForceAccess = true;
        return this;
    }

}
