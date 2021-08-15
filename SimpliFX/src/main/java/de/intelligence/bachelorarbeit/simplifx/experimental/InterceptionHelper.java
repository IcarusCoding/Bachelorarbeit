package de.intelligence.bachelorarbeit.simplifx.experimental;

import java.lang.reflect.Method;
import java.util.Arrays;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

public final class InterceptionHelper {

    public static Method getMethodForParams(String name, Object obj, Object... params) {
        return Reflection.reflect(obj).reflectMethod(name, Arrays.stream(params).map(Object::getClass)
                .toArray(Class<?>[]::new)).getReflectable();
    }

}
