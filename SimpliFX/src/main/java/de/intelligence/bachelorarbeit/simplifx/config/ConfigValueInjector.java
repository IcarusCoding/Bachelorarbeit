package de.intelligence.bachelorarbeit.simplifx.config;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import de.intelligence.bachelorarbeit.reflectionutils.FieldReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.SimpliFXConstants;
import de.intelligence.bachelorarbeit.simplifx.injection.AnnotatedFieldDetector;
import de.intelligence.bachelorarbeit.simplifx.injection.IAnnotatedFieldDetector;

public final class ConfigValueInjector {

    private final IAnnotatedFieldDetector<ConfigValue> configValueDetector;

    public ConfigValueInjector(Object instance, Object... more) {
        this.configValueDetector = new AnnotatedFieldDetector<>(ConfigValue.class, instance, more);
    }

    public void inject(PropertyRegistry registry) {
        // find all fields
        this.configValueDetector.findAllFields();
        final Map<FieldReflection, ConfigValue> fields = new LinkedHashMap<>();
        for (final Map.Entry<Object, Map<Field, ConfigValue[]>> entry : this.configValueDetector.getFieldMap().entrySet()) {
            for (final Map.Entry<Field, ConfigValue[]> fSpec : entry.getValue().entrySet()) {
                fields.put(Reflection.reflect(fSpec.getKey(), entry.getKey()).forceAccess(), fSpec.getValue()[0]);
            }
        }
        for (final Map.Entry<FieldReflection, ConfigValue> entry : fields.entrySet()) {
            final FieldReflection fieldRef = entry.getKey();
            final Field field = fieldRef.getReflectable();
            final Class<?> fieldType = SimpliFXConstants.PRIMITIVES_MAP.getOrDefault(field.getType(), field.getType());
            final ConfigValue configValue = entry.getValue();
            final String value = registry.getForKey(configValue.value(), configValue.defaultValue());
            System.out.println(value);
            if (fieldType.isAssignableFrom(String.class)) {
                fieldRef.set(value);
                continue;
            }
            if (!SimpliFXConstants.OBJECT_CONVERSION_MAP.containsKey(fieldType)) {
                System.out.println("UNSUPPORTED TYPE");
                continue;
            }
            fieldRef.set(SimpliFXConstants.OBJECT_CONVERSION_MAP.get(fieldType).apply(value));
        }
    }

}
