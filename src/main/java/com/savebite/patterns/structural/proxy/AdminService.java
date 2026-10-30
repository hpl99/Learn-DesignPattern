package com.savebite.patterns.structural.proxy;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.User;

import java.util.List;

// Pattern: Proxy
// Category: Structural
// Problem it solves here: Administrative platform operations (deleting listings, altering user roles, viewing raw audit tables) must be guarded against unauthorized invocation regardless of the caller channel.
// GoF participant role: Subject
// Used by: AdminServiceProxy, RealAdminService, AdminController
@DesignPattern(name = "Proxy", role = "Subject", description = "Subject interface for privileged platform administration")
public interface AdminService {
    void deleteListing(String currentUserRole, String listingId);
    boolean changeUserRole(String currentUserRole, String targetUserId, String newRole);
    List<User> listAllUsers(String currentUserRole);
}

