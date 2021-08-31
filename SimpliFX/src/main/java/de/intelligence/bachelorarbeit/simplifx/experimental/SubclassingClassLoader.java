package de.intelligence.bachelorarbeit.simplifx.experimental;

import java.lang.reflect.Modifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;

import de.intelligence.bachelorarbeit.simplifx.utils.Pair;

/**
 * A {@link ClassLoader} which is able to create subclasses of existing classes at runtime.
 */
public final class SubclassingClassLoader extends ClassLoader {

    private static final Logger LOG = LogManager.getLogger(SubclassingClassLoader.class);

    private final SubclassFXThreadWriter writer;

    /**
     * Instantiates a new {@link SubclassingClassLoader}.
     *
     * @param parentLoader The parent {@link ClassLoader}.
     */
    public SubclassingClassLoader(ClassLoader parentLoader) {
        super(parentLoader);
        this.writer = new SubclassFXThreadWriter(0, Opcodes.V16);
    }

    /**
     * Creates a subclass of the specified class.
     *
     * @param clazz The class from which a subclass should be derived.
     * @return The {@link Class} of the created subclass-
     */
    public Class<?> defineSubclass(Class<?> clazz) {
        if (!Modifier.isPublic(clazz.getModifiers()) || Modifier.isFinal(clazz.getModifiers())) {
            LOG.warn("Experimental features disabled for class \"{}\". Reason: Cannot create a subclass -> class must be a non final public class!", clazz.getName());
            return clazz;
        }
        final Pair<String, byte[]> classInfo = this.writer.createSubclassForClass(clazz);
        return super.defineClass(classInfo.getLeft(), classInfo.getRight(), 0, classInfo.getRight().length);
    }

}
