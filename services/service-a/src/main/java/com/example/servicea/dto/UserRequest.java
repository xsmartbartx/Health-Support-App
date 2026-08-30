package com.example.servicea.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Inbound payload for creating a user.
 */
public class UserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
