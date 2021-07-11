package de.intelligence.bachelorarbeit.simplifx.classpath.source;

import java.net.URL;

/**
 * This class encapsulates common elements of a {@link IDiscoverySource} implementation
 *
 * @author Deniz Groenhoff
 * @see de.intelligence.bachelorarbeit.simplifx.classpath.source.IDiscoverySource
 */
abstract class AbstractDiscoverySource implements IDiscoverySource {

    protected final URL sourceURL;
    protected final String pathPrefix;

    protected AbstractDiscoverySource(URL sourceURL, String pathPrefix) {
        this.sourceURL = sourceURL;
        this.pathPrefix = pathPrefix;
    }

    @Override
    public URL getSourceURL() {
        return this.sourceURL;
    }

}
