package de.intelligence.bachelorarbeit.simplifx.experimental;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.utils.Pair;

public final class SubclassFXThreadWriter {

    private static final Map<Class<?>, Integer> LOAD_MAP = Map.of(byte.class, 21, short.class, 21,
            int.class, 21, boolean.class, 21, char.class, 21, long.class, 22, float.class, 23,
            double.class, 24);

    private static final Map<Class<?>, Integer> RETURN_MAP = Map.of(byte.class, 172, short.class, 172, int.class,
            172, boolean.class, 172, char.class, 172, long.class, 173, float.class, 174, double.class,
            175, void.class, 177);

    private static final Map<Class<?>, Class<?>> PRIMITIVES_MAP = Map.of(byte.class, Byte.class, short.class, Short.class,
            int.class, Integer.class, long.class, Long.class, boolean.class, Boolean.class, char.class, Character.class,
            float.class, Float.class, double.class, Double.class);

    private static final Method VOID_INTERCEPTOR = Reflection.reflect(ExperimentalInterceptor.class)
            .reflectMethod("interceptVoid", Object.class, Method.class, Object[].class).getReflectable();

    private static final Method RETURN_INTERCEPTOR = Reflection.reflect(ExperimentalInterceptor.class)
            .reflectMethod("interceptAndReturn", Object.class, Method.class, Object[].class).getReflectable();

    private static final List<String> NAME_REGISTRY = new ArrayList<>();
    private static final char[] ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int DEFAULT_LENGTH = 10;
    private static final String IDENTIFIER = "SimpliFX";
    private static final String DELEGATOR_IDENTIFIER = "SimpliFXDelegator";

    private final int flags;
    private final int version;

    public SubclassFXThreadWriter(int flags, int version) {
        this.flags = flags;
        this.version = version;
    }

    private static void createArray(MethodVisitor visitor, Method method) {
        doPush(visitor, method.getParameterCount());
        visitor.visitTypeInsn(Opcodes.ANEWARRAY, Type.getInternalName(Object.class));
        int index = 0;
        int currentStackIndex = 1;
        for (final var param : method.getParameterTypes()) {
            visitor.visitInsn(Opcodes.DUP);
            doPush(visitor, index);
            visitor.visitVarInsn(LOAD_MAP.getOrDefault(param, Opcodes.ALOAD), currentStackIndex);
            currentStackIndex += calculateSize(param);
            if (PRIMITIVES_MAP.containsKey(param)) {
                final Method valueOfMethod = Reflection.reflect(PRIMITIVES_MAP.get(param)).reflectMethod("valueOf", param).getReflectable();
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(PRIMITIVES_MAP.get(param)), "valueOf",
                        Type.getMethodDescriptor(valueOfMethod), false);
            }
            visitor.visitInsn(Opcodes.AASTORE);
            index++;
        }
    }

    private static void doPush(MethodVisitor visitor, int index) {
        if (index < 6) {
            visitor.visitInsn(3 + index);
        } else {
            visitor.visitVarInsn(Opcodes.BIPUSH, index);
        }
    }

    private static void createSuperDelegator(ClassWriter writer, String delegatorName, Method origin) {
        final String methodDesc = Type.getMethodDescriptor(origin);
        final MethodVisitor delegatorMethodVisitor = writer.visitMethod(origin.getModifiers(), delegatorName, methodDesc,
                null, convertExceptionsToInternalTypes(origin.getExceptionTypes()));
        final int size = createDefaultInit(delegatorMethodVisitor, origin.getParameterTypes());
        delegatorMethodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(origin.getDeclaringClass()),
                origin.getName(), methodDesc, false);
        delegatorMethodVisitor.visitInsn(RETURN_MAP.getOrDefault(origin.getReturnType(), Opcodes.ARETURN));
        delegatorMethodVisitor.visitMaxs(size, size);
        delegatorMethodVisitor.visitEnd();
    }

    private static int createDefaultInit(MethodVisitor visitor, Class<?>[] parameterTypes) {
        visitor.visitVarInsn(Opcodes.ALOAD, 0); // implicit self reference
        int current = 1;
        for (final var param : parameterTypes) { // put all constructor params on stack
            visitor.visitVarInsn(LOAD_MAP.getOrDefault(param, Opcodes.ALOAD), current);
            current += calculateSize(param);
        }
        current++;
        return current;
    }

    private static int calculateSize(Class<?> clazz) {
        return (clazz.equals(long.class) || clazz.equals(double.class)) ? 2 : 1;
    }

    private static String[] convertExceptionsToInternalTypes(Class<?>[] exceptions) {
        return Arrays.stream(exceptions).map(Type::getInternalName).toArray(String[]::new);
    }

    private static String createName(String baseName, String identifier) {
        String name;
        do {
            name = baseName + "$" + identifier + "$" + SubclassFXThreadWriter.generateRandom();
        } while (NAME_REGISTRY.contains(name));
        NAME_REGISTRY.add(name);
        return name;
    }

    private static String generateRandom() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            builder.append(ALLOWED_CHARACTERS[new Random().nextInt(ALLOWED_CHARACTERS.length)]);
        }
        return builder.toString();
    }

    public synchronized Pair<String, byte[]> createSubclassForClass(Class<?> clazz) {
        final ClassWriter writer = new ClassWriter(flags);
        final String subclassName = createName(clazz.getSimpleName(), IDENTIFIER);
        writer.visit(this.version, Opcodes.ACC_PUBLIC, subclassName, null, Type.getInternalName(clazz), new String[]{});
        Arrays.stream(clazz.getConstructors()).filter(constructor -> Modifier.isPublic(constructor.getModifiers())
                || Modifier.isProtected(constructor.getModifiers()))
                .forEach(constructor -> {
                    final String conDesc = Type.getConstructorDescriptor(constructor);
                    final MethodVisitor constructorVisitor = writer.visitMethod(constructor.getModifiers(), "<init>", conDesc,
                            null, convertExceptionsToInternalTypes(constructor.getExceptionTypes()));
                    final int size = createDefaultInit(constructorVisitor, constructor.getParameterTypes());
                    constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(clazz), "<init>", conDesc, false);
                    constructorVisitor.visitInsn(Opcodes.RETURN);
                    constructorVisitor.visitMaxs(size, size);
                    constructorVisitor.visitEnd();
                });
        Arrays.stream(clazz.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(FXThread.class) && !Modifier.isFinal(m.getModifiers())
                && Modifier.isPublic(m.getModifiers())).forEach(m -> {
            final String delegatorName = createName(m.getName(), DELEGATOR_IDENTIFIER);
            createSuperDelegator(writer, delegatorName, m);
            final MethodVisitor visitor = writer.visitMethod(m.getModifiers(), m.getName(), Type.getMethodDescriptor(m),
                    null, convertExceptionsToInternalTypes(m.getExceptionTypes()));
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitLdcInsn(delegatorName);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            createArray(visitor, m);
            final Method helperMethod = Reflection.reflect(InterceptionHelper.class).reflectMethod("getMethodForParams",
                    String.class, Object.class, Object[].class).getReflectable();
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(InterceptionHelper.class), helperMethod.getName(),
                    Type.getMethodDescriptor(helperMethod), false);
            createArray(visitor, m);
            final Method interceptor = m.getReturnType().equals(Void.TYPE) ? VOID_INTERCEPTOR : RETURN_INTERCEPTOR;
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(interceptor.getDeclaringClass()),
                    interceptor.getName(), Type.getMethodDescriptor(interceptor), false);
            if (!m.getReturnType().equals(Void.TYPE)) {
                final Class<?> wrappedType = PRIMITIVES_MAP.getOrDefault(m.getReturnType(), m.getReturnType());
                visitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(wrappedType));
                if (PRIMITIVES_MAP.containsKey(m.getReturnType())) {
                    final String conversionMethod = m.getReturnType().getSimpleName() + "Value";
                    visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(wrappedType), conversionMethod,
                            Type.getMethodDescriptor(Reflection.reflect(wrappedType).reflectMethod(conversionMethod)
                                    .getReflectable()), false);
                }
            }
            visitor.visitInsn(RETURN_MAP.getOrDefault(m.getReturnType(), Opcodes.ARETURN));
            int currentMaxSize = Arrays.stream(m.getParameterTypes()).mapToInt(SubclassFXThreadWriter::calculateSize).max().orElse(1);
            visitor.visitMaxs(4 + (m.getParameterCount() > 0 ? (currentMaxSize + 2) : 0), this.calculateMaxSize(m.getParameterTypes()));
            visitor.visitEnd();
        });
        writer.visitEnd();
        final byte[] bytecode = writer.toByteArray();
        return Pair.of(subclassName, bytecode);
    }

    private int calculateMaxSize(Class<?>[] params) {
        return Arrays.stream(params).mapToInt(SubclassFXThreadWriter::calculateSize).sum() + 1;
    }

}
