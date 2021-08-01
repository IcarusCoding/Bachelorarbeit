package de.intelligence.bachelorarbeit.simplifx.localization;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import org.apache.commons.lang3.ArrayUtils;

import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;

public final class I18N implements II18N {

    private static final String NOT_FOUND = "<key not found>";

    private final ReadOnlyObjectWrapper<Locale> currentLocale;
    private final Map<Locale, CompoundResourceBundle> bundles;

    public I18N(List<CompoundResourceBundle> resourceBundles) {
        this.bundles = resourceBundles.stream()
                .collect(Collectors.toMap(CompoundResourceBundle::getLocale, Function.identity()));
        this.currentLocale = new ReadOnlyObjectWrapper<>(this.getDefaultLocale());
    }

    @Override
    public Locale getCurrentLocale() {
        return this.currentLocale.get();
    }

    @Override
    public ReadOnlyObjectProperty<Locale> currentLocaleProperty() {
        return this.currentLocale.getReadOnlyProperty();
    }

    @Override
    public Locale getDefaultLocale() {
        return Locale.ENGLISH;
    }

    @Override
    public Optional<Locale> getByLanguageSpoken(String language) {
        Locale toReturn = null;
        for (final Locale l : this.getSupportedLanguages()) {
            if (l.getDisplayLanguage(this.currentLocale.get()).equalsIgnoreCase(language)) {
                toReturn = l;
                break;
            }
        }
        return Optional.ofNullable(toReturn);
    }

    @Override
    public Set<Locale> getSupportedLanguages() {
        return new HashSet<>(this.bundles.keySet());
    }

    @Override
    public String get(String key, Object... args) {
        return this.containsKey(key) ? MessageFormat.format(this.bundles.get(this.currentLocale.get()).getString(key), args) : I18N.NOT_FOUND;
    }

    @Override
    public StringBinding createBindingForKey(String key, Object... args) {
        return Bindings.createStringBinding(() -> this.get(key, args), this.currentLocale);
    }

    @Override
    public StringBinding createObservedBinding(String key, Object... params) {
        return Bindings.createStringBinding(
                () -> this.containsKey(key) ? MessageFormat.format(this.bundles.get(this.currentLocale.get()).getString(key),
                        Arrays.stream(params).map(o -> o instanceof ObservableValue ?
                                ((ObservableValue<?>) o).getValue() : Objects.requireNonNullElse(o, "null")
                                .toString()).toArray()) : I18N.NOT_FOUND,
                ArrayUtils.addAll(Arrays.stream(params)
                        .filter(ObservableValue.class::isInstance).map(ObservableValue.class::cast)
                        .toArray(ObservableValue[]::new), this.currentLocale));
    }

    @Override
    public Map<Locale, StringBinding> createBindings() {
        return this.getSupportedLanguages().stream().collect(Collectors.toMap(Function.identity(),
                l -> Bindings.createStringBinding(() -> l.getDisplayLanguage(this.currentLocale.get()), this.currentLocale)));
    }

    @Override
    public void setupMenu(Menu menu) {
        Conditions.checkNull(menu, "menu must not be null.");
        final List<MenuItem> items = new ArrayList<>();
        this.createBindings().forEach((l, s) -> {
            final MenuItem item = new MenuItem();
            item.textProperty().bind(s);
            this.getByLanguageSpoken(item.getText()).ifPresent(lang -> item.setOnAction(e -> this.setLocale(lang)));
            items.add(item);
        });
        menu.getItems().setAll(items);
    }

    @Override
    public void setLocale(Locale locale) {
        if (this.bundles.containsKey(locale)) {
            this.currentLocale.set(locale);
        }
    }

    @Override
    public boolean containsKey(String key) {
        if (!this.bundles.containsKey(this.currentLocale.get())) {
            return false;
        }
        return this.bundles.get(this.currentLocale.get()).containsKey(key);
    }

}
