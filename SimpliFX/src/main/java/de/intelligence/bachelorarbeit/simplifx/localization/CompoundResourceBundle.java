package de.intelligence.bachelorarbeit.simplifx.localization;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public final class CompoundResourceBundle extends ResourceBundle {

    private final Locale locale;
    private final Map<String, String> resources;

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

}
