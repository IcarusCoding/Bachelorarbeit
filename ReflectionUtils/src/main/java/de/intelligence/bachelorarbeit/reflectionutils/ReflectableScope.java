package de.intelligence.bachelorarbeit.reflectionutils;

/**
 * The {@link ReflectableScope} class is the base class of every implemented reflection scope
 *
 * @author Deniz Groenhoff
 */
class ReflectableScope<T> implements Reflectable<T> {

    protected final T reflectable;

    protected ReflectableScope(T reflectable) {
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
