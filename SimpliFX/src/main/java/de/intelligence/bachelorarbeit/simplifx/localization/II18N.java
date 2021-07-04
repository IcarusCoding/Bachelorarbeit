package de.intelligence.bachelorarbeit.simplifx.localization;

import java.util.Locale;
import java.util.Set;

import javafx.beans.binding.StringBinding;

public interface II18N {

    Locale getCurrentLocale();

    Locale getDefaultLocale();

    Set<Locale> getSupportedLanguages();

    String get(String key, Object... args);

    StringBinding createBindingForKey(String key, Object... args);

    StringBinding createObservedBinding(String key, Object... params);

    void setLocale(Locale locale);

    boolean containsKey(String key);

}
