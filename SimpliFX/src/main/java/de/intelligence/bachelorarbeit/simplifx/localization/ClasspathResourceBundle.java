package de.intelligence.bachelorarbeit.simplifx.localization;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public final class ClasspathResourceBundle extends ResourceBundle {

    public ClasspathResourceBundle(String directory, String base, Locale locale) {
        final URL url = getClass().getResource(directory);
        try (URLClassLoader loader = AccessController
                .doPrivileged((PrivilegedAction<URLClassLoader>) () -> new URLClassLoader(new URL[] {url}))) {
            final ResourceBundle bundle = getBundle(base, locale, loader);
            super.setParent(bundle);
        } catch (IOException e) {
            throw new IllegalStateException("Could not close URLClassLoader!", e);
        }
    }

    @Override
    protected Object handleGetObject(String key) {
        return parent.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return parent.getKeys();
    }

}
