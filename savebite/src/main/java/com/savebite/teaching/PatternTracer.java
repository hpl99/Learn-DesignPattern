package com.savebite.teaching;

import com.savebite.annotation.DesignPattern;
import io.javalin.http.sse.SseClient;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Pattern: Singleton & Observer
// Category: Creational & Behavioral
// Problem it solves here: Coordinates global recording of all GoF pattern invocations and dispatches them to connected SSE clients.
// GoF participant role: Singleton (Single instance) & Subject (Notifies TraceListeners)
// Used by: Every pattern class and teaching SSE controller
@DesignPattern(name = "Singleton", role = "Singleton", description = "Thread-safe tracer instance coordinating trace events")
@DesignPattern(name = "Observer", role = "Subject", description = "Publishes trace events to registered listeners and SSE clients")
public class PatternTracer {
    private static final int MAX_BUFFER_SIZE = 200;
    private final List<TraceListener> listeners = new CopyOnWriteArrayList<>();
    private final List<SseClient> sseClients = new CopyOnWriteArrayList<>();
    private final Deque<TraceEvent> eventHistory = new ArrayDeque<>();

    private PatternTracer() {}

    private static class Holder {
        private static final PatternTracer INSTANCE = new PatternTracer();
    }

    public static PatternTracer getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized void record(String pattern, String className, String method, String message) {
        record(pattern, className, method, message, "");
    }

    public synchronized void record(String pattern, String className, String method, String message, String details) {
        TraceEvent event = new TraceEvent(pattern, className, method, message, details);
        if (eventHistory.size() >= MAX_BUFFER_SIZE) {
            eventHistory.pollFirst();
        }
        eventHistory.addLast(event);

        // Notify in-process listeners
        for (TraceListener listener : listeners) {
            try {
                listener.onTraceEvent(event);
            } catch (Exception ignored) {}
        }

        // Broadcast to SSE clients
        for (SseClient client : sseClients) {
            try {
                client.sendEvent("trace", event);
            } catch (Exception e) {
                sseClients.remove(client);
            }
        }
    }

    public void addListener(TraceListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TraceListener listener) {
        listeners.remove(listener);
    }

    public void addSseClient(SseClient client) {
        sseClients.add(client);
        client.onClose(() -> sseClients.remove(client));
    }

    public synchronized List<TraceEvent> getRecentEvents() {
        return Collections.unmodifiableList(new ArrayList<>(eventHistory));
    }

    public synchronized void clear() {
        eventHistory.clear();
    }
}

