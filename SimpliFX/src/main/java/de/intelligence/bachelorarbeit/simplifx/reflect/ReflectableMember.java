package de.intelligence.bachelorarbeit.simplifx.reflect;

import java.lang.reflect.Member;

abstract class ReflectableMember<T extends Member> extends ReflectableType<T> {

    protected boolean shouldForceAccess;
    protected Object accessor;

    protected ReflectableMember(T reflectable) {
        super(reflectable);
    }

    protected ReflectableMember(T reflectable, Object accessor) {
        super(reflectable);
        this.accessor = accessor;
    }

    public abstract <E> E forceAccess();

    public void setAccessor(Object accessor) {
        this.accessor = accessor;
    }

}
