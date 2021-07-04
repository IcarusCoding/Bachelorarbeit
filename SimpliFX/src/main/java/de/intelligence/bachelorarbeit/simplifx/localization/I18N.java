package de.intelligence.bachelorarbeit.simplifx.localization;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import org.apache.commons.lang3.ArrayUtils;

public final class I18N implements II18N {

    private final ObjectProperty<Locale> currentLocale;
    private final Map<Locale, CompoundResourceBundle> bundles;

    public I18N(List<CompoundResourceBundle> resourceBundles) {
        this.bundles = resourceBundles.stream()
                .collect(Collectors.toMap(CompoundResourceBundle::getLocale, Function.identity()));
        this.currentLocale = new SimpleObjectProperty<>(this.getDefaultLocale());
    }

    @Override
    public Locale getCurrentLocale() {
        return this.currentLocale.get();
    }

    @Override
    public Locale getDefaultLocale() {
        return Locale.ENGLISH;
    }

    @Override
    public Set<Locale> getSupportedLanguages() {
        return new HashSet<>(this.bundles.keySet());
    }

    @Override
    public String get(String key, Object... args) {
        //TODO argument count mismatch
        return MessageFormat.format(this.bundles.get(this.currentLocale.get()).getString(key), args);
    }

    @Override
    public StringBinding createBindingForKey(String key, Object... args) {
        return Bindings.createStringBinding(() -> this.get(key, args), this.currentLocale);
    }

    @Override
    public StringBinding createObservedBinding(String key, Object... params) {
        return Bindings.createStringBinding(
                () -> MessageFormat.format(this.bundles.get(this.currentLocale.get()).getString(key),
                        Arrays.stream(params)
                                .map(o -> o instanceof ObservableValue ? ((ObservableValue<?>) o).getValue() :
                                        Objects.requireNonNullElse(o, "null").toString()).toArray()),
                ArrayUtils.addAll(Arrays.stream(params)
                        .filter(ObservableValue.class::isInstance).map(ObservableValue.class::cast)
                        .toArray(ObservableValue[]::new), currentLocale));
    }

    @Override
    public void setLocale(Locale locale) {
        if (this.bundles.containsKey(locale)) {
            this.currentLocale.set(locale);
        }
    }

    @Override
    public boolean containsKey(String key) {
        return this.bundles.get(this.currentLocale.get()).containsKey(key);
    }

  /*  public static Map<Locale, ResourceBundle> findAllBundlesWithBaseName(String directory, String base,
                                                                         Consumer<Exception> exceptionConsumer) {
        final List<Locale> foundLocales = FileUtils.getFilesFromClasspathDirectory(directory).stream().map(Path::toString)
                .filter(c -> "properties".equals(FileUtils.getFileExtension(c)) && c.contains("_"))
                .map(FileUtils::getNameWithoutExtension).filter(n -> n.contains(base)).map(n -> n.split("_"))
                .filter(arr -> arr.length == 2).map(arr -> arr[1])
                .map(Locale::forLanguageTag).distinct()
                .collect(Collectors.toList());
        final Map<Locale, ResourceBundle> result = new HashMap<>();
        foundLocales.forEach(locale -> {
            ResourceBundle bundle = null;
            try {
                bundle = new ClasspathResourceBundle(directory, base, locale);
            } catch (Exception ex) {
                exceptionConsumer.accept(ex);
            }
            if(bundle != null) {
                result.put(locale, bundle);
            }
        });
        return result;
    }*/

}
