package de.intelligence.bachelorarbeit.simplifx.css;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.intelligence.bachelorarbeit.reflectionutils.ConstructorReflection;
import de.intelligence.bachelorarbeit.reflectionutils.FieldReflection;
import de.intelligence.bachelorarbeit.reflectionutils.MethodReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

public final class CssMetaDataAdapter {

    private static final Logger LOG = LogManager.getLogger(CssMetaDataAdapter.class);

    public static void createStaticMetaData(Class<?> clazz, Field field) {
        if (!Reflection.reflect(Node.class).canAccept(clazz)) {
            LOG.warn("Class does not inherit from Node, skipping: {}.", clazz.getSimpleName());
            return;
        }
        final AtomicReference<MethodReflection> methodRef = new AtomicReference<>();
        Reflection.reflect(clazz).iterateMethods(m -> m.getName().equals("getClassCssMetaData"), m -> {
            if (methodRef.get() == null) {
                methodRef.set(Reflection.reflectStatic(m));
            }
        });
        final FieldReflection fieldRef = Reflection.reflectStatic(field).forceAccess();
        if (!fieldRef.isAnnotationPresent(StyleProperty.class)) {
            LOG.warn("Invalid field detected (@CssProperty missing): {}.", field);
            return;
        }
        final ParameterizedType parameterizedType = ((ParameterizedType) ((ParameterizedType) fieldRef.getReflectable().getGenericType()).getActualTypeArguments()[0]);
        if (!fieldRef.getReflectable().getType().equals(List.class) && parameterizedType.getRawType() == CssMetaData.class
                && Arrays.stream(parameterizedType.getActualTypeArguments()).allMatch(t -> t instanceof WildcardType)
                && ((WildcardType) parameterizedType.getActualTypeArguments()[0]).getUpperBounds()[0] == Styleable.class
                && ((WildcardType) parameterizedType.getActualTypeArguments()[1]).getUpperBounds()[0] == Object.class) {
            LOG.warn("Invalid field detected (Field type is not List<CssMetaData<? extends Styleable, ?>): {}.", field);
            return;
        }
        if (fieldRef.get() != null) {
            LOG.warn("Invalid field detected (Field is already initialized): {}.", field);
            return;
        }
        final List<CssMetaData<? extends Styleable, ?>> list = new ArrayList<>();
        final CssProperty[] properties = fieldRef.getReflectable().getAnnotationsByType(CssProperty.class);
        Arrays.stream(properties).forEach(property -> {
            final Class<? extends StyleConverter<?, ?>> converterClass = property.converterClass();
            final Optional<MethodReflection> getInstanceRef = Reflection.reflect(converterClass).hasMethod("getInstance");
            if (getInstanceRef.isEmpty()) {
                LOG.warn("Invalid field detected (Could not initialize SizeConverter instance): {}.", converterClass.getSimpleName());
                return;
            }
            final StyleConverter<?, ?> converterInstance = getInstanceRef.get().invokeUnsafe();
            final CssMetaData<Styleable, ?> constructedMetaData =
                    Reflection.reflect(CssMetaDataProxy.class.asSubclass(CssMetaData.class))
                            .findConstructor(String.class, StyleConverter.class, Class.class, String.class)
                            .instantiateUnsafeAndGet(property.property(), converterInstance, clazz, property.localPropertyField());
            list.add(constructedMetaData);
        });
        list.addAll(methodRef.get().invokeUnsafe());
        Reflection.reflectStatic(field).forceAccess().set(list);
    }

    public static Optional<FieldReflection> validateField(Object obj, String field) {
        return Reflection.reflect(obj).hasField(field).filter(fieldRef -> Reflection.reflect(StyleableProperty.class)
                .canAccept(fieldRef.getReflectable().getType()));
    }

    public static void bindMetaData(Object obj, Field field, CssProperty[] props) {
        Arrays.stream(props).forEach(prop -> {
            if (prop.localPropertyField().isBlank()) {
                return;
            }
            final Optional<FieldReflection> localFieldRefOpt = CssMetaDataAdapter.validateField(obj, prop.localPropertyField());
            if (localFieldRefOpt.isEmpty()) {
                LOG.warn("Specified property field \"{}\" not available in class: {}.", prop.localPropertyField(), obj.getClass().getSimpleName());
                return;
            }
            if (localFieldRefOpt.get().forceAccess().get() != null) {
                return;
            }
            final Class<?> propertyClassInterface = localFieldRefOpt.get().getReflectable().getType();
            final Class<?> propertyClassImpl;
            final String propertyClassSimpleImpl = propertyClassInterface.getPackageName() + ".Simple" + propertyClassInterface.getSimpleName();
            try {
                //TODO maybe switch case + anonymous own
                propertyClassImpl = Class.forName(propertyClassSimpleImpl);
            } catch (ClassNotFoundException e) {
                LOG.warn("Could not instantiate field: \"{}\". Reason: No implementation for \"{}\" was found", prop.localPropertyField(), propertyClassSimpleImpl);
                return;
            }
            final Optional<ConstructorReflection> conRefProp = Reflection.reflect(propertyClassImpl)
                    .hasConstructor(CssMetaData.class, Object.class, String.class);
            if (conRefProp.isEmpty()) {
                LOG.warn("Class \"{}\" has no appropriate constructor", propertyClassSimpleImpl);
                return;
            }
            final List<CssMetaData<? extends Styleable, ?>> metaData = Reflection.reflectStatic(field).forceAccess().getUnsafe();
            metaData.forEach(m -> {
                if (m.getProperty().equals(prop.property())) {
                    final StyleableProperty<?> testProperty = Reflection.reflect(propertyClassImpl).findConstructor(CssMetaData.class, Object.class, String.class)
                            .instantiateUnsafeAndGet(m, obj, prop.localPropertyField());
                    Reflection.reflect(obj).reflectField(prop.localPropertyField()).forceAccess().set(testProperty);
                }
            });
        });
    }

}
