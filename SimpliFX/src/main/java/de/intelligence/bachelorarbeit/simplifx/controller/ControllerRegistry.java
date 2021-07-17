package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//TODO exceptions on invalid access
public final class ControllerRegistry {

    private static final Set<String> registeredIds = new HashSet<>();
    private static final Map<String, ControllerGroupContext> contextMap = new HashMap<>();
    private static final Map<Class<?>, String> controllerToIdMap = new HashMap<>();

    private ControllerRegistry() {
        throw new UnsupportedOperationException();
    }

    public static String getGroupId(Class<?> clazz) {
        return controllerToIdMap.get(clazz);
    }

    public static ControllerGroupContext getContextFor(String groupId) {
        return contextMap.get(groupId);
    }

    public static void register(String groupId, ControllerGroupContext ctx) {
        registeredIds.add(groupId);
        contextMap.put(groupId, ctx);
    }

    public static boolean isRegistered(Class<?> clazz) {
        return controllerToIdMap.containsKey(clazz);
    }

    public static boolean isRegistered(String groupId) {
        return registeredIds.contains(groupId);
    }

    public static void removeController(Class<?> clazz) {
        controllerToIdMap.remove(clazz);
    }

    public static boolean removeGroup(String groupId) {
        if (controllerToIdMap.values().stream().noneMatch(groupId::equals)) {
            return false;
        }
        registeredIds.remove(groupId);
        contextMap.remove(groupId);
        return true;
    }

    public static void addController(String groupId, Class<?> clazz) {
        if (registeredIds.contains(groupId) && !controllerToIdMap.containsKey(clazz)) {
            controllerToIdMap.put(clazz, groupId);
        }
    }

}
