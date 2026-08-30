package com.example.servicea.dto;

import com.example.servicea.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Inbound payload for updating only an appointment's status.
 */
public record AppointmentStatusRequest(
        @NotNull(message = "Status is required")
        AppointmentStatus status) {
}
