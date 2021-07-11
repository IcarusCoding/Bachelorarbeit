package de.intelligence.bachelorarbeit.simplifx.classpath.source;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import lombok.extern.log4j.Log4j2;

import de.intelligence.bachelorarbeit.simplifx.classpath.ClasspathDiscoveryException;
import de.intelligence.bachelorarbeit.simplifx.utils.CloseableIterator;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
import de.intelligence.bachelorarbeit.simplifx.utils.Prefix;

/**
 * A jar file based classpath source
 *
 * @author Deniz Groenhoff
 * @see de.intelligence.bachelorarbeit.simplifx.classpath.source.IDiscoverySource
 * @see de.intelligence.bachelorarbeit.simplifx.classpath.source.AbstractDiscoverySource
 */
@Log4j2
public final class JarDiscoverySource extends AbstractDiscoverySource {

    public JarDiscoverySource(URL sourceURL, String pathPrefix) {
        super(sourceURL, pathPrefix);
    }

    @Override
    public CloseableIterator<ResourceContext> iterator() {
        return new JarSourceIterator();
    }

    private final class JarSourceIterator implements CloseableIterator<ResourceContext> {

        private final JarInputStream jarIn;
        private ResourceContext currentCtx;

        private JarSourceIterator() {
            try {
                this.jarIn = new JarInputStream(JarDiscoverySource.super.sourceURL.openConnection().getInputStream());
            } catch (IOException ex) {
                throw new ClasspathDiscoveryException(
                        "There was an exception while trying to create a new InputStream:", ex);
            }
        }

        private ResourceContext getNextCtx() {
            JarEntry currentEntry = null;
            do {
                try {
                    currentEntry = this.jarIn.getNextJarEntry();
                } catch (IOException ignored) {
                    break;
                }
            } while (currentEntry != null && (currentEntry.isDirectory() ||
                    !currentEntry.getName().startsWith(JarDiscoverySource.super.pathPrefix)));
            final JarEntry fCurrentEntry = currentEntry;
            return Conditions.returnIfNotNullReturn(currentEntry, () -> new ResourceContext(new URL(Prefix.JAR_PREFIX +
                    JarDiscoverySource.super.sourceURL.toString() + Prefix.JAR_SEPARATOR + fCurrentEntry.getName()),
                    new URI(fCurrentEntry.getName())));
        }

        @Override
        public boolean hasNext() {
            final boolean hasNext = (this.currentCtx = this.getNextCtx()) != null;
            if (!hasNext) {
                this.close();
            }
            return hasNext;
        }

        @Override
        public ResourceContext next() {
            return this.currentCtx;
        }

        @Override
        public void close() {
            Conditions.doIfNotNull(this.jarIn, this.jarIn::close,
                    ex -> JarDiscoverySource.log.error("Could not close resource: ", ex));
        }

    }

}
