package de.intelligence.bachelorarbeit.simplifx.event;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import de.intelligence.bachelorarbeit.reflectionutils.Reflection;
import de.intelligence.bachelorarbeit.simplifx.annotation.EventHandler;
import de.intelligence.bachelorarbeit.simplifx.events.InvalidEvent;
import de.intelligence.bachelorarbeit.simplifx.utils.AnnotatedMethodCache;
import de.intelligence.bachelorarbeit.simplifx.utils.Conditions;
import de.intelligence.bachelorarbeit.simplifx.utils.Pair;

public class EventEmitterImpl implements IEventEmitter {

    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<EventListener>> handlerMethods;

    private final ThreadLocal<Queue<Pair<Object, List<EventListener>>>> threadLocalQueue;

    public EventEmitterImpl() {
        this.handlerMethods = new ConcurrentHashMap<>();
        this.threadLocalQueue = ThreadLocal.withInitial(ArrayDeque::new);
    }

    @Override
    public void emit(Object obj) {
        Conditions.checkNull(obj);
        if (this.emit0(obj)) {
            return;
        }
        this.emit0(new InvalidEvent(obj));
    }

    @Override
    public void register(Object obj) {
        Conditions.checkNull(obj);
        for (final Method m : AnnotatedMethodCache.getMethodsAnnotatedBy(EventHandler.class, obj.getClass())) {
            if (m.getParameterCount() != 1) {
                // TODO warning
                continue;
            }
            final Class<?> eventParamType = m.getParameterTypes()[0];
            if (!this.handlerMethods.containsKey(eventParamType)) {
                this.handlerMethods.put(eventParamType, new CopyOnWriteArrayList<>());
            }
            this.handlerMethods.get(eventParamType)
                    .add(new EventListener(obj, m, m.getAnnotation(EventHandler.class).priority()));
        }
    }

    @Override
    public void unregister(Object obj) {
        Conditions.checkNull(obj);
        for (final Method m : AnnotatedMethodCache.getMethodsAnnotatedBy(EventHandler.class, obj.getClass())) {
            if (m.getParameterCount() != 1) {
                continue;
            }
            final Class<?> eventParamType = m.getParameterTypes()[0];
            if (!this.handlerMethods.containsKey(eventParamType)) {
                continue;
            }
            this.handlerMethods.get(eventParamType).removeIf(e -> e.method.equals(m));
        }
    }

    private boolean emit0(Object obj) {
        if (!this.handlerMethods.containsKey(obj.getClass())) {
            return false;
        }
        final Queue<Pair<Object, List<EventListener>>> queue = this.threadLocalQueue.get();
        queue.offer(Pair.of(obj, this.handlerMethods.get(obj.getClass()).stream()
                .sorted(Comparator.comparing(EventListener::priority).reversed()).collect(Collectors.toList())));
        final AtomicReference<Pair<Object, List<EventListener>>> currentPair = new AtomicReference<>(queue.poll());
        while (currentPair.get() != null) {
            currentPair.get().getRight().forEach(
                    listener -> Reflection.reflect(listener.method, listener.subObj).forceAccess()
                            .invoke(currentPair.get().getLeft()));
            currentPair.set(queue.poll());
        }
        return true;
    }

    private record EventListener(Object subObj, Method method, int priority) {

    }

}
