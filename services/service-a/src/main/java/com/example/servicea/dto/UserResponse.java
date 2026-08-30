package com.example.servicea.dto;

import com.example.servicea.model.AppUser;

/**
 * Outbound representation of a user (never exposes the JPA entity directly).
 */
public record UserResponse(Long id, String name) {

    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getName());
    }
}
