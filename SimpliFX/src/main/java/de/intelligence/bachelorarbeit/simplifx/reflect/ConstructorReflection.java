package de.intelligence.bachelorarbeit.simplifx.reflect;

import java.lang.reflect.Constructor;

public final class ConstructorReflection extends ReflectableMember<Constructor<?>> {

    ConstructorReflection(Constructor<?> constructor) {
        super(constructor);
    }

    public InstanceReflection instantiate() {
        return new InstanceReflection(instantiateAndGet());
    }

    public <T> T instantiateUnsafeAndGet(Object... args) {
        return Reflection.unsafeCast(instantiateAndGet(args));
    }

    public Object instantiateAndGet(Object... args) {
        return Reflection.makeAccessibleAndExecute(super.reflectable, null, super.shouldForceAccess,
                () -> super.reflectable.newInstance(args));
    }

    @Override
    public ConstructorReflection forceAccess() {
        super.shouldForceAccess = true;
        return this;
    }

}
