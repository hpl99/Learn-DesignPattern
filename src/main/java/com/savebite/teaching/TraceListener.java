package com.savebite.teaching;

/**
 * Observer interface for listening to GoF Pattern execution events.
 */
public interface TraceListener {
    void onTraceEvent(TraceEvent event);
}

