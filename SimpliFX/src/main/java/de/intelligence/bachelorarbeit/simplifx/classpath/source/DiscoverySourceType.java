package de.intelligence.bachelorarbeit.simplifx.classpath.source;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.function.BiFunction;

import de.intelligence.bachelorarbeit.simplifx.classpath.ClasspathDiscoveryException;
import de.intelligence.bachelorarbeit.simplifx.utils.Prefix;

/**
 * This enum class allows the creation of currently supported classpath sources
 *
 * @author Deniz Grenhoff
 * @see IDiscoverySource
 */
public enum DiscoverySourceType {

    FILE(Prefix.FILE_SCHEME, FileDiscoverySource::new),
    JAR(Prefix.JAR_SCHEME, JarDiscoverySource::new);

    private final String scheme;
    private final BiFunction<URL, String, IDiscoverySource> sourceCreator;

    DiscoverySourceType(String scheme, BiFunction<URL, String, IDiscoverySource> sourceCreator) {
        this.scheme = scheme;
        this.sourceCreator = sourceCreator;
    }

    /**
     * Creates a new {@link IDiscoverySource} by the {@link URL} protocol
     *
     * @param url        The {@link URL} to the classpath source
     * @param pathPrefix The package filter
     * @return A new {@link IDiscoverySource} instance based on the specified parameters
     */
    public static IDiscoverySource createDiscoverySource(URL url, String pathPrefix) {
        for (final DiscoverySourceType type : DiscoverySourceType.values()) {
            if (url.getProtocol().equals(type.scheme)) {
                return switch (type) {
                    case JAR -> {
                        try {
                            yield type.sourceCreator
                                    .apply(((JarURLConnection) url.openConnection()).getJarFileURL(), pathPrefix);
                        } catch (IOException ex) {
                            throw new ClasspathDiscoveryException("Could not open connection: ", ex);
                        }
                    }
                    case FILE -> type.sourceCreator.apply(url, pathPrefix);
                };
            }
        }
        throw new ClasspathDiscoveryException("Unsupported URL scheme: " + url.getProtocol());
    }

    public String getScheme() {
        return this.scheme;
    }

    public BiFunction<URL, String, IDiscoverySource> getSourceCreator() {
        return this.sourceCreator;
    }

}
