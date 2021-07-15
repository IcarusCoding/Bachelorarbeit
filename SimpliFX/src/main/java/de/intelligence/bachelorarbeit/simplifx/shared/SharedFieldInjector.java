package de.intelligence.bachelorarbeit.simplifx.shared;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;

import de.intelligence.bachelorarbeit.reflectionutils.FieldReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.injection.AnnotatedFieldDetector;
import de.intelligence.bachelorarbeit.simplifx.injection.IAnnotatedFieldDetector;

public class SharedFieldInjector {

    private static final Pattern KEY_EXTRACTOR = Pattern.compile("\\p{Lu}");

    private final IAnnotatedFieldDetector<Shared> sharedDetector;

    public SharedFieldInjector(Object instance, Object... more) {
        this.sharedDetector = new AnnotatedFieldDetector<>(Shared.class, instance, more);
    }

    public void inject(SharedResources resources) {
        // find all fields
        this.sharedDetector.findAllFields((f, a) -> f.forceAccess().get() == null);
        final Map<FieldReflection, Shared> fields = new LinkedHashMap<>();
        for (final Map.Entry<Object, Map<Field, Shared[]>> entry : this.sharedDetector.getFieldMap().entrySet()) {
            for (final Map.Entry<Field, Shared[]> fSpec : entry.getValue().entrySet()) {
                fields.put(Reflection.reflect(fSpec.getKey(), entry.getKey()).forceAccess(), fSpec.getValue()[0]);
            }
        }
        for (final Map.Entry<FieldReflection, Shared> entry : fields.entrySet()) {
            final FieldReflection fieldRef = entry.getKey();
            final Field field = fieldRef.getReflectable();
            final Class<?> fieldType = field.getType();
            if (fieldType.equals(SharedResources.class)) {
                fieldRef.set(resources);
                continue;
            }
            final String fieldName = field.getName();
            String key = entry.getValue().value();
            if (key.isBlank()) {
                final Matcher matcher = SharedFieldInjector.KEY_EXTRACTOR.matcher(fieldName);
                final int indexOfFirstUppercase = matcher.find() ? matcher.start() : -1;
                key = indexOfFirstUppercase == -1 ? fieldName : fieldName.substring(0, indexOfFirstUppercase);
            }
            System.out.println("KEY: " + key);
            SharedReference<?> ref = resources.getForName(key);
            if (fieldType.equals(SharedReference.class) || fieldType.isAssignableFrom(ReadOnlyObjectProperty.class)) {
                final Class<?> clazzType = this.validateGenericParameter(field);
                if (clazzType == null) {
                    continue;
                }
                if (ref != null) {
                    if (!clazzType.equals(ref.getType())) {
                        System.out.println("Unable to accept type ERROR");
                        continue;
                    }
                } else {
                    ref = SharedReference.empty(clazzType);
                    resources.create(key, ref);
                }
            }
            if (fieldType.equals(SharedReference.class)) {
                fieldRef.set(ref);
                continue;
            }
            if (fieldType.isAssignableFrom(ReadOnlyObjectProperty.class)) {
                fieldRef.set(ref.asProperty());
                continue;
            }
            //TODO remove maybe -- not very useful
            if (ReadOnlyProperty.class.isAssignableFrom(fieldType)) {
                System.out.println("PROPERTY");
                final ObjectPropertyConverter converter = new ObjectPropertyConverter();
                if (ref == null) { // need to create own reference
                    if (!converter.canConvert(fieldType)) {
                        // field type not supported
                        continue;
                    }
                    final Class<?> type = converter.getConversionType(fieldType);
                    ref = SharedReference.empty(type);
                    resources.create(key, ref);
                }
                fieldRef.set(converter.convert(ref.getType(), ref.asProperty()));
            }
        }
    }

    private Class<?> validateGenericParameter(Field field) {
        final Type genericArg;
        if (field.getGenericType() instanceof ParameterizedType type) {
            genericArg = type.getActualTypeArguments()[0];
            if (genericArg instanceof WildcardType) {
                System.out.println("WILDCARD ERROR");
                // wild card disallowed
                return null;
            }
            if (!(genericArg instanceof Class<?>)) {
                System.out.println("INVALID TYPE ERROR");
                // type cant be applied
                return null;
            }
        } else {
            System.out.println("RAW USE ERROR");
            // raw use is disallowed
            return null;
        }
        return (Class<?>) genericArg;
    }

}
