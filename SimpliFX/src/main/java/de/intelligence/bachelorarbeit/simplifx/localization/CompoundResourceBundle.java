package de.intelligence.bachelorarbeit.simplifx.localization;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * A {@link ResourceBundle} implementation which combines multiple {@link ResourceBundle} instances into a single one.
 */
public final class CompoundResourceBundle extends ResourceBundle {

    private final Locale locale;
    private final Map<String, String> resources;

    /**
     * Creates a new {@link CompoundResourceBundle} instance.
     *
     * @param locale  The {@link Locale} of this {@link CompoundResourceBundle}.
     * @param bundles The {@link List} of {@link ResourceBundle} instances which will be combined into a single one.
     */
    public CompoundResourceBundle(Locale locale, List<ResourceBundle> bundles) {
        this.locale = locale;
        this.resources = new HashMap<>();
        bundles.forEach(bundle -> bundle.getKeys().asIterator()
                .forEachRemaining(key -> this.resources.put(key, bundle.getString(key))));
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    @Override
    protected Object handleGetObject(String key) {
        return this.resources.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(this.resources.keySet());
    }

    @Override
    public boolean containsKey(String key) {
        return this.resources.containsKey(key);
    }

}
