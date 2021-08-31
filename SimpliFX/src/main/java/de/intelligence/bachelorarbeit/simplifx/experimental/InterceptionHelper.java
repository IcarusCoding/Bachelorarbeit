package de.intelligence.bachelorarbeit.simplifx.experimental;

import java.lang.reflect.Method;
import java.util.Arrays;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

public final class InterceptionHelper {

    /**
     * A helper method to get a method by its name and parameters.
     *
     * @param name   The name of the method.
     * @param obj    The object in which the method should be found.
     * @param params The method parameters.
     * @return The found method.
     */
    public static Method getMethodForParams(String name, Object obj, Object... params) {
        return Reflection.reflect(obj).reflectMethod(name, Arrays.stream(params).map(Object::getClass)
                .toArray(Class<?>[]::new)).getReflectable();
    }

}
