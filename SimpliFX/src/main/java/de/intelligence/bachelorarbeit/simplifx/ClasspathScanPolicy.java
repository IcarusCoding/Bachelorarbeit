package de.intelligence.bachelorarbeit.simplifx;

/**
 * An enum which represents the types of classpath scanning.
 *
 * @author Deniz Groenhoff
 */
public enum ClasspathScanPolicy {

    /**
     * The classpath scanner will only search in a local package.
     */
    LOCAL,
    /**
     * The classpath scanner will try to search in the whole classpath.
     */
    GLOBAL

}
