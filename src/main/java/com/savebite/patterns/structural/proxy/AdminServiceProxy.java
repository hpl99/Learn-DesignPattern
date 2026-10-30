package com.savebite.patterns.structural.proxy;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.User;
import com.savebite.teaching.PatternTracer;

import java.util.List;

// Pattern: Proxy
// Category: Structural
// Problem it solves here: Protection Proxy guarding administrative actions. Intercepts method calls, verifies the caller's role is strictly ADMIN, logs access violations, and blocks unauthorized callers before they reach RealAdminService.
// GoF participant role: Proxy
// Used by: AdminController
@DesignPattern(name = "Proxy", role = "Proxy", description = "Protection Proxy enforcing RBAC checks on Admin operations")
public class AdminServiceProxy implements AdminService {
    private final AdminService realService;

    public AdminServiceProxy(AdminService realService) {
        this.realService = realService;
    }

    private void checkAdminAccess(String currentUserRole, String operation) {
        if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
            PatternTracer.getInstance().record(
                "Proxy",
                "AdminServiceProxy",
                "checkAdminAccess",
                "ACCESS DENIED (403): Role '" + currentUserRole + "' attempted prohibited operation '" + operation + "'"
            );
            throw new SecurityException("Access Denied: Only ADMIN role can perform '" + operation + "'. Current role: " + currentUserRole);
        }

        PatternTracer.getInstance().record(
            "Proxy",
            "AdminServiceProxy",
            "checkAdminAccess",
            "ACCESS GRANTED (200): Verified ADMIN role for operation '" + operation + "'"
        );
    }

    @Override
    public void deleteListing(String currentUserRole, String listingId) {
        checkAdminAccess(currentUserRole, "deleteListing");
        realService.deleteListing(currentUserRole, listingId);
    }

    @Override
    public boolean changeUserRole(String currentUserRole, String targetUserId, String newRole) {
        checkAdminAccess(currentUserRole, "changeUserRole");
        return realService.changeUserRole(currentUserRole, targetUserId, newRole);
    }

    @Override
    public List<User> listAllUsers(String currentUserRole) {
        checkAdminAccess(currentUserRole, "listAllUsers");
        return realService.listAllUsers(currentUserRole);
    }
}

