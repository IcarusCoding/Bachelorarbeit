package de.intelligence.bachelorarbeit.simplifx.css;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.Node;

import com.sun.javafx.fxml.BeanAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.intelligence.bachelorarbeit.reflectionutils.ConstructorReflection;
import de.intelligence.bachelorarbeit.reflectionutils.FieldReflection;
import de.intelligence.bachelorarbeit.reflectionutils.MethodReflection;
import de.intelligence.bachelorarbeit.reflectionutils.Reflection;

public final class CssMetaDataAdapter {

    private static final Logger LOG = LogManager.getLogger(CssMetaDataAdapter.class);

    private CssMetaDataAdapter() {
        throw new UnsupportedOperationException();
    }

    public static void createStaticMetaData(Class<?> clazz, Field field) {
        if (!Reflection.reflect(Node.class).canAccept(clazz)) {
            LOG.warn("Class does not inherit from Node, skipping: {}.", clazz.getSimpleName());
            return;
        }
        final AtomicReference<MethodReflection> methodRef = new AtomicReference<>();
        Reflection.reflect(clazz).iterateMethods(m -> "getClassCssMetaData".equals(m.getName()), m -> {
            if (methodRef.get() == null) {
                methodRef.set(Reflection.reflectStatic(m));
            }
        });
        final FieldReflection fieldRef = Reflection.reflectStatic(field).forceAccess();
        if (!fieldRef.isAnnotationPresent(CssProperty.class)) {
            LOG.warn("Invalid field detected (@CssProperty missing): {}.", field);
            return;
        }
        final ParameterizedType parameterizedType = ((ParameterizedType) ((ParameterizedType) fieldRef.getReflectable().getGenericType()).getActualTypeArguments()[0]);
        if (!fieldRef.getReflectable().getType().equals(List.class) && parameterizedType.getRawType() == CssMetaData.class
                && Arrays.stream(parameterizedType.getActualTypeArguments()).allMatch(WildcardType.class::isInstance)
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
                LOG.warn("Invalid field detected (Could not initialize StyleConverter instance): {}.", converterClass.getSimpleName());
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
        return Reflection.reflect(obj).hasField(field).filter(fieldRef -> Reflection.reflect(StyleableObjectProperty.class)
                .canAccept(fieldRef.getReflectable().getType()));
    }

    public static void bindMetaData(Object obj, Field field, CssProperty[] props) {
        Arrays.stream(props).forEach(prop -> {
            if (prop.localPropertyField().isBlank()) {
                return;
            }
            final Optional<FieldReflection> localFieldRefOpt = CssMetaDataAdapter.validateField(obj, prop.localPropertyField());
            if (localFieldRefOpt.isEmpty()) {
                LOG.warn("Specified property field \"{}\" invalid or not available in class or superclass: {}.", prop.localPropertyField(), obj.getClass().getSimpleName());
                return;
            }
            if (localFieldRefOpt.get().forceAccess().get() != null) {
                return;
            }
            final BeanAdapter adapter = new BeanAdapter(obj);
            AtomicReference<Property<?>> bindToProp = new AtomicReference<>();
            if (!prop.bindTo().isBlank()) {
                if (!adapter.containsKey(prop.bindTo())) {
                    LOG.warn("Specified property field \"{}\" invalid or not available in class: {}.", prop.bindTo(), obj.getClass().getSimpleName());
                    return;
                }
                final ObservableValue<?> val = adapter.getPropertyModel(prop.bindTo());
                if (val instanceof Property<?> p) {
                    bindToProp.set(p);
                }
            }
            final Class<?> propertyClassInterface = localFieldRefOpt.get().getReflectable().getType();
            final Class<?> propertyClassImpl;
            final String propertyClassSimpleImpl = propertyClassInterface.getPackageName() + ".Simple" + propertyClassInterface.getSimpleName();
            try {
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
                    final StyleableProperty<?> testProperty = conRefProp.get()
                            .instantiateUnsafeAndGet(m, obj, prop.localPropertyField());
                    Reflection.reflect(obj).reflectField(prop.localPropertyField()).forceAccess().set(testProperty);
                    if (bindToProp.get() != null) {
                        final List<MethodReflection> found = new ArrayList<>();
                        Reflection.reflect(bindToProp.get()).iterateMethods(mRef -> "bind".equals(mRef.getReflectable().getName())
                                && mRef.getReflectable().getParameterCount() == 1
                                && mRef.getReflectable().getParameterTypes()[0].equals(ObservableValue.class), found::add);
                        if (!found.isEmpty()) {
                            found.get(0).invoke(testProperty);
                        }
                    }
                }
            });
        });
    }

}
