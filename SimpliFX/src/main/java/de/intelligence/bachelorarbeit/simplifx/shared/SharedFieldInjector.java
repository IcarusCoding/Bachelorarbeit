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

import de.intelligence.bachelorarbeit.reflectionutils.FieldReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.exception.IllegalSharedFieldException;
import de.intelligence.bachelorarbeit.simplifx.injection.AnnotatedFieldDetector;
import de.intelligence.bachelorarbeit.simplifx.injection.IAnnotatedFieldDetector;

/**
 * An injector for fields annotated with the {@link Shared} annotation.
 *
 * @author Deniz Groenhoff
 */
public class SharedFieldInjector {

    private static final Pattern KEY_EXTRACTOR = Pattern.compile("\\p{Lu}");

    private final IAnnotatedFieldDetector<Shared> sharedDetector;

    /**
     * Creates a new injector.
     *
     * @param instance The instance in which the fields should get injected.
     * @param more     Other instances in which the fields should get injected.
     */
    public SharedFieldInjector(Object instance, Object... more) {
        this.sharedDetector = new AnnotatedFieldDetector<>(Shared.class, instance, more);
    }

    private static Class<?> validateGenericParameter(Field field) {
        final Type genericArg;
        if (field.getGenericType() instanceof ParameterizedType type) {
            genericArg = type.getActualTypeArguments()[0];
            if (genericArg instanceof WildcardType) {
                throw new IllegalSharedFieldException(field, "Wildcard types are not supported.");
            }
            // generic shared resources can be supported but will cause problems at runtime due to type erasure
            /*if (genericArg instanceof ParameterizedType subParam && subParam.getRawType() instanceof Class<?> c) {
                return c;
            }*/
            if (!(genericArg instanceof Class<?>)) {
                throw new IllegalSharedFieldException(field, "Generic shared resources are currently not supported.");
            }
        } else {
            throw new IllegalSharedFieldException(field, "Raw use is disallowed.");
        }
        return (Class<?>) genericArg;
    }

    /**
     * Injects the specified {@link SharedResources} into found fields.
     *
     * @param resources The {@link SharedResources} which should get injected.
     */
    public void inject(SharedResources resources) {
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
            SharedReference<?> ref = resources.getForName(key);
            if (fieldType.equals(SharedReference.class) || fieldType.isAssignableFrom(ReadOnlyObjectProperty.class)) {
                final Class<?> clazzType = SharedFieldInjector.validateGenericParameter(field);
                if (ref != null) {
                    if (!clazzType.equals(ref.getType())) {
                        throw new IllegalSharedFieldException(field, "Shared resource with id \"" + key
                                + "\" expects field type " + ref.getType().getName() + " but received "
                                + clazzType.getName() + ".");
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
            }
        }
    }

}
