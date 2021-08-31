package de.intelligence.bachelorarbeit.simplifx.localization;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Menu;

/**
 * An interface for a dynamic localization ability.
 *
 * @author Deniz Groenhoff
 */
public interface II18N {

    /**
     * Retrieves the current active {@link Locale}.
     *
     * @return The current active {@link Locale}.
     */
    Locale getCurrentLocale();

    /**
     * Retrieves the current active {@link Locale} as a {@link ReadOnlyObjectProperty}.
     *
     * @return The current active {@link Locale} as a {@link ReadOnlyObjectProperty}.
     */
    ReadOnlyObjectProperty<Locale> currentLocaleProperty();

    /**
     * Retrieves the default {@link Locale}.
     *
     * @return The default {@link Locale}.
     */
    Locale getDefaultLocale();

    /**
     * Retrieves a {@link Locale} by its spoken language.
     *
     * @param language The spoken language for which a {@link Locale} instance should be found.
     * @return An {@link Optional} containing the found {@link Locale} or {@link Optional#empty()} if none was found.
     */
    Optional<Locale> getByLanguageSpoken(String language);

    /**
     * Retrieves all currently supported languages.
     *
     * @return A {@link Set} of all currently supported languages.
     */
    Set<Locale> getSupportedLanguages();

    /**
     * Gets the translated {@link String} for the specified key and automatically inserts the provided arguments.
     *
     * @param key  The key for which a translated {@link String} should be retrieved.
     * @param args The optional arguments for the translated {@link String}.
     * @return The translated {@link String} for the specified key and arguments.
     */
    String get(String key, Object... args);

    /**
     * Creates a new {@link StringBinding} for the specified key and arguments which will be translated on a language update.
     *
     * @param key  The key for the translated {@link StringBinding}.
     * @param args The optional arguments for the translated {@link StringBinding}.
     * @return The {@link StringBinding} which will be translated on language update.
     */
    StringBinding createBindingForKey(String key, Object... args);

    /**
     * Creates a new {@link StringBinding} for the specified key and arguments which will be translated on a language update.
     * An update will also occur if one of the provided arguments is a {@link javafx.beans.property.Property} and will get externally updated.
     *
     * @param key  The key for the translated {@link StringBinding}.
     * @param args The optional arguments for the translated {@link StringBinding}.
     * @return The {@link StringBinding} which will be translated on language update.
     */
    StringBinding createObservedBinding(String key, Object... args);

    /**
     * Creates a {@link Map} for all supported spoken languages.
     *
     * @return A {@link Map} for all supported spoken languages.
     */
    Map<Locale, StringBinding> createBindings();

    /**
     * Populates a {@link Menu} with all supported spoken languages.
     *
     * @param menu A {@link Menu} with all supported spoken languages.
     */
    void setupMenu(Menu menu);

    /**
     * Sets the current {@link Locale}.
     *
     * @param locale The current {@link Locale}.
     */
    void setLocale(Locale locale);

    /**
     * Checks if the currently loaded {@link ResourceBundle} contains the specified key.
     *
     * @param key The key.
     * @return If the currently loaded {@link ResourceBundle} contains the specified key.
     */
    boolean containsKey(String key);

}
