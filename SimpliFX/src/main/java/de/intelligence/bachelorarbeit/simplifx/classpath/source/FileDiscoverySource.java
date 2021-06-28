package de.intelligence.bachelorarbeit.simplifx.classpath.source;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.intelligence.bachelorarbeit.simplifx.classpath.ClasspathDiscoveryException;
import de.intelligence.bachelorarbeit.simplifx.utils.CloseableIterator;
import de.intelligence.bachelorarbeit.simplifx.utils.CloseableWrappedIterator;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
import de.intelligence.bachelorarbeit.simplifx.utils.Prefix;

/**
 * A system directory based classpath source
 *
 * @author Deniz Groenhoff
 * @see de.intelligence.bachelorarbeit.simplifx.classpath.source.IDiscoverySource
 * @see de.intelligence.bachelorarbeit.simplifx.classpath.source.AbstractDiscoverySource
 */
public final class FileDiscoverySource extends AbstractDiscoverySource {

    protected FileDiscoverySource(URL sourceURL, String pathPrefix) {
        super(sourceURL, pathPrefix);
    }

    @Override
    public CloseableIterator<ResourceContext> iterator() {
        try {
            return new CloseableWrappedIterator<>(
                    Files.walk(Paths.get(super.sourceURL.toURI())).filter(Files::isRegularFile)
                            .map(p -> Conditions.nullOnException(() -> {
                                final URI uri = p.toUri();
                                return new ResourceContext(uri.toURL(), new URI(super.pathPrefix.isBlank() ? "" :
                                        super.pathPrefix + Prefix.FILE_SEPARATOR + super.sourceURL.toURI()
                                                .relativize(uri)
                                                .toString()));
                            })).iterator());
        } catch (IOException | URISyntaxException ex) {
            throw new ClasspathDiscoveryException("Failed to create iterator: ", ex);
        }
    }

}
