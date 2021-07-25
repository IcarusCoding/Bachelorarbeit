package de.intelligence.bachelorarbeit.simplifx;

import java.util.Map;
import java.util.function.Function;

public final class SimpliFXConstants {

    static final byte[] BANNER = {
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 10, 32, 44, 45, 45, 45, 46, 32, 32, 32, 44, 45, 45, 46, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 44, 45, 45, 46, 32, 44, 45, 45, 46, 32,
            44, 45, 45, 45, 45, 45, 45, 46, 32, 44, 45, 45, 46, 32, 32, 32, 44, 45, 45, 46, 32, 10, 39, 32, 32, 32, 46,
            45, 39, 32, 32, 96, 45, 45, 39, 32, 44, 45, 45, 44, 45, 45, 44, 45, 45, 46, 32, 32, 44, 45, 45, 45, 46, 32,
            32, 124, 32, 32, 124, 32, 96, 45, 45, 39, 32, 124, 32, 32, 46, 45, 45, 45, 39, 32, 32, 92, 32, 32, 96, 46,
            39, 32, 32, 47, 32, 32, 10, 96, 46, 32, 32, 96, 45, 46, 32, 32, 44, 45, 45, 46, 32, 124, 32, 32, 32, 32, 32,
            32, 32, 32, 124, 32, 124, 32, 46, 45, 46, 32, 124, 32, 124, 32, 32, 124, 32, 44, 45, 45, 46, 32, 124, 32,
            32, 96, 45, 45, 44, 32, 32, 32, 32, 46, 39, 32, 32, 32, 32, 92, 32, 32, 32, 10, 46, 45, 39, 32, 32, 32, 32,
            124, 32, 124, 32, 32, 124, 32, 124, 32, 32, 124, 32, 32, 124, 32, 32, 124, 32, 124, 32, 39, 45, 39, 32, 39,
            32, 124, 32, 32, 124, 32, 124, 32, 32, 124, 32, 124, 32, 32, 124, 96, 32, 32, 32, 32, 32, 47, 32, 32, 46,
            39, 46, 32, 32, 92, 32, 32, 10, 96, 45, 45, 45, 45, 45, 39, 32, 32, 96, 45, 45, 39, 32, 96, 45, 45, 96, 45,
            45, 96, 45, 45, 39, 32, 124, 32, 32, 124, 45, 39, 32, 32, 96, 45, 45, 39, 32, 96, 45, 45, 39, 32, 96, 45,
            45, 39, 32, 32, 32, 32, 32, 39, 45, 45, 39, 32, 32, 32, 39, 45, 45, 39, 32, 10, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 96, 45, 45, 39, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32
    };

    public static final Map<Class<?>, Function<String, Object>> OBJECT_CONVERSION_MAP =
            Map.of(Boolean.class, Boolean::parseBoolean, Byte.class, Byte::parseByte, Double.class, Double::parseDouble,
                    Float.class, Float::parseFloat, Integer.class, Integer::parseInt, Long.class, Long::parseLong,
                    Short.class, Short::parseShort);

    public static final Map<Class<?>, Class<?>> PRIMITIVES_MAP =
            Map.of(byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class,
                    boolean.class, Boolean.class, char.class, Character.class, float.class, Float.class, double.class,
                    Double.class);

    private SimpliFXConstants() {
        throw new UnsupportedOperationException();
    }

}
