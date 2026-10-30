package com.savebite.repository;

import com.savebite.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    User save(User user);
    List<User> findAll();
    boolean updateRole(String userId, String newRole);
    void deleteById(String id);
}

