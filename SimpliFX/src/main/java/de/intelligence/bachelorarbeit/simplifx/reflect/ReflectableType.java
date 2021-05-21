package de.intelligence.bachelorarbeit.simplifx.reflect;

class ReflectableType<T> implements Reflectable<T> {

    protected final T reflectable;

    protected ReflectableType(T reflectable) {
        this.reflectable = reflectable;
    }

    protected static boolean matchArguments(Class<?>[] wanted, Class<?>[] actual) {
        boolean found = true;
        for (var index = 0; index < wanted.length; index++) {
            final Class<?> wantedType = wanted[index];
            final Class<?> actualType = actual[index];
            if (!actualType.isAssignableFrom(wantedType)) {
                found = Reflection.PRIMITIVE_CHECK.test(actualType, wantedType);
            }
            if (!found) {
                break;
            }
        }
        return found;
    }

    @Override
    public T getReflectable() {
        return this.reflectable;
    }

}
