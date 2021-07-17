package de.intelligence.bachelorarbeit.simplifx.shared;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;

public final class ObjectPropertyConverter {

    private static final Map<Class<? extends ReadOnlyProperty<?>>, Class<?>> CONVERSION_MAP;

    static {
        CONVERSION_MAP = new HashMap<>();
        CONVERSION_MAP.put(ReadOnlyStringProperty.class, String.class);
        CONVERSION_MAP.put(ReadOnlyIntegerProperty.class, Integer.class);
        CONVERSION_MAP.put(ReadOnlyBooleanProperty.class, Boolean.class);
        CONVERSION_MAP.put(ReadOnlyDoubleProperty.class, Double.class);
        CONVERSION_MAP.put(ReadOnlyFloatProperty.class, Float.class);
        CONVERSION_MAP.put(ReadOnlyLongProperty.class, Long.class);
    }

    public boolean canConvert(Class<?> clazz) {
        return ObjectPropertyConverter.CONVERSION_MAP.containsKey(clazz);
    }

    public Class<?> getConversionType(Class<?> clazz) {
        return ObjectPropertyConverter.CONVERSION_MAP.get(clazz);
    }

    public Property<?> convert(Class<?> type, ReadOnlyObjectProperty<?> property) {
        if (type.equals(String.class)) {
            return createStringProperty(property);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private StringProperty createStringProperty(ReadOnlyObjectProperty<?> property) {
        final ReadOnlyObjectProperty<String> casted = (ReadOnlyObjectProperty<String>) property;
        return new StringPropertyBase() {
            {
                super.bind(casted);
            }

            @Override
            public Object getBean() {
                return null;
            }

            @Override
            public String getName() {
                return casted.getName();
            }
        };
    }

}
