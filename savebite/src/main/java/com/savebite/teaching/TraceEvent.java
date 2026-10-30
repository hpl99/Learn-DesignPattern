package com.savebite.teaching;

import java.time.Instant;

public class TraceEvent {
    private final String id;
    private final Instant timestamp;
    private final String pattern;
    private final String className;
    private final String method;
    private final String message;
    private final String details;

    public TraceEvent(String pattern, String className, String method, String message, String details) {
        this.id = java.util.UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.pattern = pattern;
        this.className = className;
        this.method = method;
        this.message = message;
        this.details = details != null ? details : "";
    }

    public String getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
    public String getPattern() { return pattern; }
    public String getClassName() { return className; }
    public String getMethod() { return method; }
    public String getMessage() { return message; }
    public String getDetails() { return details; }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s#%s - %s", timestamp, pattern, className, method, message);
    }
}

