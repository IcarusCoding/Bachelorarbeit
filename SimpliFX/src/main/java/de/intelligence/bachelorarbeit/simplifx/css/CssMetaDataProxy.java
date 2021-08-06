package de.intelligence.bachelorarbeit.simplifx.css;

import java.util.Objects;
import java.util.Optional;

import javafx.beans.property.Property;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;

import de.intelligence.bachelorarbeit.reflectionutils.FieldReflection;

public final class CssMetaDataProxy<S extends Styleable, V> extends CssMetaData<S, V> {

    private final Class<S> styleable;
    private final String fieldName;

    public CssMetaDataProxy(String s, StyleConverter<?, V> styleConverter, Class<S> styleable, String fieldName) {
        super(s, styleConverter);
        this.styleable = styleable;
        this.fieldName = fieldName;
    }

    private Optional<FieldReflection> getField(Styleable styleable) {
        return CssMetaDataAdapter.validateField(this.styleable.cast(styleable), this.fieldName);
    }

    @Override
    public boolean isSettable(Styleable styleable) {
        if (!this.styleable.isInstance(styleable)) {
            return false;
        }
        final Optional<FieldReflection> localFieldRefOpt = this.getField(styleable);
        if (localFieldRefOpt.isEmpty()) {
            return false;
        }
        final Property<?> property = localFieldRefOpt.get().forceAccess().getUnsafe();
        if (property == null) {
            return false;
        }
        return !property.isBound();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StyleableProperty<V> getStyleableProperty(S styleable) {
        if (!this.styleable.isInstance(styleable)) {
            return null;
        }
        final Optional<FieldReflection> localFieldRefOpt = this.getField(styleable);
        if (localFieldRefOpt.isEmpty()) {
            return null;
        }
        final StyleableProperty<?> property = localFieldRefOpt.get().forceAccess().getUnsafe();
        if (property == null) {
            return null;
        }
        return (StyleableProperty<V>) property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CssMetaDataProxy<?, ?> that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return styleable.equals(that.styleable) && fieldName.equals(that.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), styleable, fieldName);
    }
}
