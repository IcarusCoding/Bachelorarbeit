package de.intelligence.bachelorarbeit.simplifx.classpath;

/**
 * A {@link RuntimeException} which can be used with classpath related operations
 *
 * @author Deniz Groenhoff
 * @see java.lang.RuntimeException
 */
public final class ClasspathDiscoveryException extends RuntimeException {

    public ClasspathDiscoveryException() {
        super();
    }

    public ClasspathDiscoveryException(String message) {
        super(message);
    }

    public ClasspathDiscoveryException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
