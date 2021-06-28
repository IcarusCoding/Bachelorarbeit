package de.intelligence.bachelorarbeit.simplifx.classpath.source;

import java.net.URI;
import java.net.URL;

/**
 * This class represents a resource/class in the classpath
 *
 * @author Deniz Groenhoff
 */
public class ResourceContext {

    private final URL absoluteURL;
    private final URI relativeURI;
    private String fileType;

    public ResourceContext(URL absoluteURL, URI relativeURI) {
        this.absoluteURL = absoluteURL;
        this.relativeURI = relativeURI;
        final String uri = relativeURI.toString();
        int idx = uri.lastIndexOf('.');
        if (idx != -1) {
            this.fileType = uri.substring(idx).replace(".", "");
        }
    }

    /**
     * Retrieves the absolute {@link URL} from this context
     *
     * @return The absolute {@link URL}
     */
    public URL getAbsoluteURL() {
        return this.absoluteURL;
    }


    /**
     * Retrieves the relative {@link URI} from this context
     *
     * @return The relative {@link URI}
     */
    public URI getRelativeURI() {
        return this.relativeURI;
    }

    /**
     * Retrieves the file type from this context
     *
     * @return The file type
     */
    public String getFileType() {
        return this.fileType;
    }

}
