package de.intelligence.bachelorarbeit.simplifx.experimental;

import java.lang.reflect.Modifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;

import de.intelligence.bachelorarbeit.simplifx.utils.Pair;

public final class SubclassingClassLoader extends ClassLoader {

    private static final Logger LOG = LogManager.getLogger(SubclassingClassLoader.class);

    private final SubclassFXThreadWriter writer;

    public SubclassingClassLoader(ClassLoader parentLoader) {
        super(parentLoader);
        this.writer = new SubclassFXThreadWriter(0, Opcodes.V16);
    }

    public Class<?> defineSubclass(Class<?> clazz) {
        if (!Modifier.isPublic(clazz.getModifiers()) || Modifier.isFinal(clazz.getModifiers())) {
            LOG.warn("Experimental features disabled for class \"{}\". Reason: Cannot create a subclass -> class must be a non final public class!", clazz.getName());
            return clazz;
        }
        final Pair<String, byte[]> classInfo = this.writer.createSubclassForClass(clazz);
        return super.defineClass(classInfo.getLeft(), classInfo.getRight(), 0, classInfo.getRight().length);
    }

}
