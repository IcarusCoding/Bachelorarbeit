package de.intelligence.bachelorarbeit.simplifx.classpath.source;

import java.net.URL;

import de.intelligence.bachelorarbeit.simplifx.utils.CloseableIterable;

/**
 * This class represents an iterable classpath source (e.g. jar file, directory)
 *
 * @author Deniz Groenhoff
 * @see java.lang.Iterable
 * @see de.intelligence.bachelorarbeit.simplifx.utils.CloseableIterable
 */
public interface IDiscoverySource extends CloseableIterable<ResourceContext> {

    /**
     * Retrieves the absolute path to the source
     *
     * @return The absolute path to the source
     */
    URL getSourceURL();

}
