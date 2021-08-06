package de.intelligence.bachelorarbeit.simplifx.utils;

/**
 * An utility class which provides constants for common used prefixes and separators.
 *
 * @author Deniz Groenhoff
 */
public final class Prefix {

    public static final String FILE_SCHEME = "file";
    public static final String FILE_PREFIX = "file:";
    public static final String FILE_SEPARATOR = "/";
    public static final char FILE_SEPARATOR_C = '/';

    public static final String JAR_SCHEME = "jar";
    public static final String JAR_PREFIX = "jar:";
    public static final String JAR_SEPARATOR = "!/";

    public static final String XML_FILE_EXTENSION = ".xml";
    public static final String PROPERTIES_FILE_EXTENSION = ".properties";

    private Prefix() {
        throw new UnsupportedOperationException();
    }

}
