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

    @Override
    public T getReflectable() {
        return this.reflectable;
    }

}
