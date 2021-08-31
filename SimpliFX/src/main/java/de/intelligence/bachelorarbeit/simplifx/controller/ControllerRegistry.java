package de.intelligence.bachelorarbeit.simplifx.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A global registry for all controllers and controller groups.
 */
final class ControllerRegistry {

    private static final Set<String> registeredIds = new HashSet<>();
    private static final Map<String, ControllerGroupContext> contextMap = new HashMap<>();
    private static final Map<Class<?>, String> controllerToIdMap = new HashMap<>();

    private ControllerRegistry() {
        throw new UnsupportedOperationException();
    }

    static String getGroupId(Class<?> clazz) {
        return controllerToIdMap.get(clazz);
    }

    static ControllerGroupContext getContextFor(String groupId) {
        return contextMap.get(groupId);
    }

    static void register(String groupId, ControllerGroupContext ctx) {
        registeredIds.add(groupId);
        contextMap.put(groupId, ctx);
    }

    static boolean isRegistered(Class<?> clazz) {
        return controllerToIdMap.containsKey(clazz);
    }

    static boolean isRegistered(String groupId) {
        return registeredIds.contains(groupId);
    }

    static void removeController(Class<?> clazz) {
        controllerToIdMap.remove(clazz);
    }

    static boolean removeGroup(String groupId) {
        if (controllerToIdMap.values().stream().anyMatch(groupId::equals)) {
            return false;
        }
        registeredIds.remove(groupId);
        contextMap.remove(groupId);
        return true;
    }

    static void addController(String groupId, Class<?> clazz) {
        if (registeredIds.contains(groupId)) {
            controllerToIdMap.putIfAbsent(clazz, groupId);
        }
    }

}
