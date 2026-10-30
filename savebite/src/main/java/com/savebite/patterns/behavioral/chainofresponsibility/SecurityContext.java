package com.savebite.patterns.behavioral.chainofresponsibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityContext {
    private final String path;
    private final String method;
    private final String ipAddress;
    private final Map<String, String> headers;
    private String token;
    private String userId;
    private String userRole;
    private String userEmail;
    private String jti;
    private boolean authenticated = false;
    private boolean authorized = true;
    private int httpStatus = 200;
    private String errorMessage = null;
    private boolean terminated = false;
    private final List<HandlerExecutionStep> executionTrace = new ArrayList<>();

    public record HandlerExecutionStep(String handlerName, boolean passed, String message) {}

    public SecurityContext(String path, String method, String ipAddress, Map<String, String> headers) {
        this.path = path;
        this.method = method;
        this.ipAddress = ipAddress;
        this.headers = new HashMap<>();
        if (headers != null) {
            headers.forEach((k, v) -> this.headers.put(k.toLowerCase(), v));
        }
    }

    public void addTrace(String handlerName, boolean passed, String message) {
        executionTrace.add(new HandlerExecutionStep(handlerName, passed, message));
    }

    public void terminate(int status, String message) {
        this.httpStatus = status;
        this.errorMessage = message;
        this.terminated = true;
    }

    public String getPath() { return path; }
    public String getMethod() { return method; }
    public String getIpAddress() { return ipAddress; }
    public String getHeader(String name) { return headers.get(name.toLowerCase()); }
    public void setHeader(String name, String value) { headers.put(name.toLowerCase(), value); }
    public Map<String, String> getHeaders() { return headers; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getJti() { return jti; }
    public void setJti(String jti) { this.jti = jti; }

    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }

    public boolean isAuthorized() { return authorized; }
    public void setAuthorized(boolean authorized) { this.authorized = authorized; }

    public int getHttpStatus() { return httpStatus; }
    public String getErrorMessage() { return errorMessage; }
    public boolean isTerminated() { return terminated; }
    public List<HandlerExecutionStep> getExecutionTrace() { return executionTrace; }
}
