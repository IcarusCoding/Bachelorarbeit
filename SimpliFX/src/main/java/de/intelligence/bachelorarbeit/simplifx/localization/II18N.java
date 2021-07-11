package de.intelligence.bachelorarbeit.simplifx.localization;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface II18N {

    Locale getCurrentLocale();

    ReadOnlyObjectProperty<Locale> currentLocaleProperty();

    Locale getDefaultLocale();

    Optional<Locale> getByLanguageSpoken(String language);

    Set<Locale> getSupportedLanguages();

    String get(String key, Object... args);

    StringBinding createBindingForKey(String key, Object... args);

    StringBinding createObservedBinding(String key, Object... params);

    void setLocale(Locale locale);

    boolean containsKey(String key);

}
