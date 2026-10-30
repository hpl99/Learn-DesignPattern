package com.savebite.patterns.structural.proxy;

import com.savebite.annotation.DesignPattern;
import com.savebite.model.User;
import com.savebite.repository.AuditRepository;
import com.savebite.repository.ListingRepository;
import com.savebite.repository.UserRepository;
import com.savebite.teaching.PatternTracer;

import java.util.List;

// Pattern: Proxy
// Category: Structural
// Problem it solves here: Real execution logic for admin actions. Executes mutations directly against repositories.
// GoF participant role: RealSubject
// Used by: AdminServiceProxy
@DesignPattern(name = "Proxy", role = "RealSubject", description = "Real administrative service executing domain mutations")
public class RealAdminService implements AdminService {
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final AuditRepository auditRepository;

    public RealAdminService(UserRepository userRepository, ListingRepository listingRepository, AuditRepository auditRepository) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.auditRepository = auditRepository;
    }

    @Override
    public void deleteListing(String currentUserRole, String listingId) {
        listingRepository.deleteById(listingId);
        auditRepository.log("admin", "DELETE_LISTING", "Listing " + listingId + " was permanently removed by admin.");
        PatternTracer.getInstance().record(
            "Proxy",
            "RealAdminService",
            "deleteListing",
            "RealSubject executed delete for listing: " + listingId
        );
    }

    @Override
    public boolean changeUserRole(String currentUserRole, String targetUserId, String newRole) {
        boolean updated = userRepository.updateRole(targetUserId, newRole);
        auditRepository.log("admin", "CHANGE_USER_ROLE", "User " + targetUserId + " role updated to " + newRole);
        PatternTracer.getInstance().record(
            "Proxy",
            "RealAdminService",
            "changeUserRole",
            "RealSubject updated role for user " + targetUserId + " -> " + newRole
        );
        return updated;
    }

    @Override
    public List<User> listAllUsers(String currentUserRole) {
        PatternTracer.getInstance().record(
            "Proxy",
            "RealAdminService",
            "listAllUsers",
            "RealSubject retrieved all registered users"
        );
        return userRepository.findAll();
    }
}

